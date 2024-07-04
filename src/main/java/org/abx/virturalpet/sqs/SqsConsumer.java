package org.abx.virturalpet.sqs;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;

public class SqsConsumer implements DisposableBean {

    private static final Logger logger = LoggerFactory.getLogger(SqsConsumer.class);
    private final SqsClient sqsClient;
    private final String queueUrl;
    private final MessageProcessor messageProcessor;
    private final ExecutorService executorService;
    private volatile boolean running = true;

    public SqsConsumer(SqsClient sqsClient, String queueUrl, MessageProcessor messageProcessor) {
        this.sqsClient = sqsClient;
        this.queueUrl = queueUrl;
        this.messageProcessor = messageProcessor;
        this.executorService = Executors.newFixedThreadPool(8);
    }

    @PostConstruct
    public void start() {
        executorService.submit(this::pollMessages);
    }

    public void pollMessages() {
        while (running && !Thread.currentThread().isInterrupted()) {
            try {
                ReceiveMessageRequest receiveMessageRequest = ReceiveMessageRequest.builder()
                        .queueUrl(queueUrl)
                        .maxNumberOfMessages(10)
                        .waitTimeSeconds(20)
                        .build();

                List<Message> messages =
                        sqsClient.receiveMessage(receiveMessageRequest).messages();

                for (Message message : messages) {
                    try {
                        logger.info("Received message: {}", message.body());

                        // Process the message
                        messageProcessor.processMessage(message);

                        // Delete the message after processing
                        deleteMessage(message);
                    } catch (Exception e) {
                        logger.error("Failed to process message: {}", message.body(), e);
                    }
                }
            } catch (Exception e) {
                logger.error("Error while polling messages", e);
            }
        }
    }

    public void deleteMessage(Message message) {
        DeleteMessageRequest deleteMessageRequest = DeleteMessageRequest.builder()
                .queueUrl(queueUrl)
                .receiptHandle(message.receiptHandle())
                .build();
        sqsClient.deleteMessage(deleteMessageRequest);
    }

    @Override
    public void destroy() {
        stop();
    }

    public void stop() {
        running = false;
        executorService.shutdownNow();
        try {
            if (!executorService.awaitTermination(30, TimeUnit.SECONDS)) {
                logger.warn("ExecutorService did not terminate in the specified time.");
                List<Runnable> droppedTasks = executorService.shutdownNow();
                logger.warn(
                        "ExecutorService was abruptly shut down. {} tasks will not be executed.", droppedTasks.size());
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("ExecutorService termination interrupted", e);
        }
    }
}
