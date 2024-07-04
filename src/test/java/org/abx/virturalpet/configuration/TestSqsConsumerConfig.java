package org.abx.virturalpet.configuration;

import org.abx.virturalpet.sqs.GenImageSqsMessageProcessor;
import org.abx.virturalpet.sqs.MessageProcessor;
import org.abx.virturalpet.sqs.SqsConsumer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.services.sqs.SqsClient;

@Configuration
public class TestSqsConsumerConfig {
    @Bean
    public GenImageSqsMessageProcessor genImageSqsMessageProcessor() {
        return new GenImageSqsMessageProcessor();
    }

    @Bean
    public SqsConsumer sqsConsumer(
            SqsClient sqsClient,
            @Qualifier("ImageJobSqsQueueName") String queueUrl,
            MessageProcessor messageProcessor) {
        return new SqsConsumer(sqsClient, queueUrl, messageProcessor);
    }

    @Bean
    @Qualifier("ImageJobSqsQueueName")
    public String imageJobSqsQueueName() {
        return "virtual-pet-queue";
    }
}
