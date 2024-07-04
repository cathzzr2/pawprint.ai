package org.abx.virturalpet.sqs;

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;
import software.amazon.awssdk.services.sqs.model.DeleteMessageResponse;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageResponse;

@ExtendWith(MockitoExtension.class)
public class SqsConsumerTest {

    @Mock
    private SqsClient sqsClient;

    @Mock
    private MessageProcessor messageProcessor;

    @Captor
    private ArgumentCaptor<ReceiveMessageRequest> receiveMessageRequestCaptor;

    @Captor
    private ArgumentCaptor<DeleteMessageRequest> deleteMessageRequestCaptor;

    @InjectMocks
    private SqsConsumer sqsConsumer;

    private final String queueUrl = "https://sqs.us-east-1.amazonaws.com/123456789012/my-queue";

    @BeforeEach
    void before() {
        sqsConsumer = new SqsConsumer(sqsClient, queueUrl, messageProcessor);
    }

    @AfterEach
    void after() {
        sqsConsumer.stop();
    }

    @Test
    void testPollMessages() throws InterruptedException {
        // Prepare mock responses
        Message message1 = Message.builder()
                .body("Test message 1")
                .receiptHandle("handle1")
                .build();
        Message message2 = Message.builder()
                .body("Test message 2")
                .receiptHandle("handle2")
                .build();
        ReceiveMessageResponse receiveMessageResponse =
                ReceiveMessageResponse.builder().messages(message1, message2).build();
        DeleteMessageResponse deleteMessageResponse =
                DeleteMessageResponse.builder().build();

        AtomicInteger receiveMessageCount = new AtomicInteger(0);

        when(sqsClient.receiveMessage(ArgumentMatchers.any(ReceiveMessageRequest.class)))
                .thenAnswer(invocation -> {
                    if (receiveMessageCount.incrementAndGet() <= 1) {
                        return receiveMessageResponse;
                    } else {
                        return ReceiveMessageResponse.builder()
                                .messages(Collections.emptyList())
                                .build();
                    }
                });

        when(sqsClient.deleteMessage(ArgumentMatchers.any(DeleteMessageRequest.class)))
                .thenAnswer(invocation -> deleteMessageResponse);

        sqsConsumer.start();

        // Use a CountDownLatch to wait for the messages to be processed
        CountDownLatch latch = new CountDownLatch(2);
        doAnswer(invocation -> {
                    latch.countDown();
                    return null;
                })
                .when(messageProcessor)
                .processMessage(ArgumentMatchers.any(Message.class));

        // Wait for the latch to count down to zero
        boolean completed = latch.await(5, TimeUnit.SECONDS);

        // Verify interactions
        verify(sqsClient, atLeastOnce()).receiveMessage(receiveMessageRequestCaptor.capture());
        verify(sqsClient, times(2)).deleteMessage(deleteMessageRequestCaptor.capture());
        verify(messageProcessor, times(2)).processMessage(ArgumentMatchers.any(Message.class));

        // Verify the received message request
        List<ReceiveMessageRequest> receiveMessageRequests = receiveMessageRequestCaptor.getAllValues();

        Assertions.assertEquals(queueUrl, receiveMessageRequests.get(0).queueUrl());
        Assertions.assertEquals(10, receiveMessageRequests.get(0).maxNumberOfMessages());
        Assertions.assertEquals(20, receiveMessageRequests.get(0).waitTimeSeconds());

        // Verify the delete message requests
        List<DeleteMessageRequest> deleteMessageRequests = deleteMessageRequestCaptor.getAllValues();
        Assertions.assertEquals(queueUrl, deleteMessageRequests.get(0).queueUrl());
        Assertions.assertEquals("handle1", deleteMessageRequests.get(0).receiptHandle());
        Assertions.assertEquals(queueUrl, deleteMessageRequests.get(1).queueUrl());
        Assertions.assertEquals("handle2", deleteMessageRequests.get(1).receiptHandle());

        // Ensure that the latch counted down to zero
        Assertions.assertTrue(completed, "Messages were not processed in time");
    }

    @Test
    void testDeleteMessage() {
        Message message = Message.builder().receiptHandle("handle1").build();
        sqsConsumer.deleteMessage(message);

        verify(sqsClient).deleteMessage(deleteMessageRequestCaptor.capture());
        Assertions.assertEquals(queueUrl, deleteMessageRequestCaptor.getValue().queueUrl());
        Assertions.assertEquals("handle1", deleteMessageRequestCaptor.getValue().receiptHandle());
    }
}
