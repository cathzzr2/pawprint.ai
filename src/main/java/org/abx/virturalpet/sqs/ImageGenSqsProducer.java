package org.abx.virturalpet.sqs;

import org.abx.virturalpet.dto.ImageGenSqsDto;
import org.abx.virturalpet.exception.SqsProducerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

@Service
public class ImageGenSqsProducer implements SqsProducerInterface<ImageGenSqsDto> {

    private final Logger logger = LoggerFactory.getLogger(ImageGenSqsProducer.class);
    private final SqsClient sqsClient;
    private final String queueName;
    private final String queueUrl;

    public ImageGenSqsProducer(SqsClient sqsClient, @Qualifier("ImageJobSqsQueueName") String queueName) {
        this.sqsClient = sqsClient;
        this.queueName = queueName;
        this.queueUrl = getQueueUrl();
    }

    @Override
    public void sendMessage(ImageGenSqsDto imageGenSqsDto) {
        SendMessageRequest sendMsgRequest = SendMessageRequest.builder()
                .queueUrl(queueUrl)
                .messageBody(imageGenSqsDto.toString())
                .delaySeconds(5)
                .build();

        try {
            sqsClient.sendMessage(sendMsgRequest);
        } catch (Exception e) {
            logger.error("Error sending message to SQS: {}", e.getMessage());
            throw new SqsProducerException("Error sending message to SQS", e);
        }
    }

    private String getQueueUrl() {
        GetQueueUrlRequest getQueueRequest =
                GetQueueUrlRequest.builder().queueName(queueName).build();
        return sqsClient.getQueueUrl(getQueueRequest).queueUrl();
    }
}
