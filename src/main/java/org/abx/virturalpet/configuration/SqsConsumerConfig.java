package org.abx.virturalpet.configuration;

import org.abx.virturalpet.service.sqs.GenImageSqsMessageProcessor;
import org.abx.virturalpet.service.sqs.MessageProcessor;
import org.abx.virturalpet.service.sqs.SqsConsumer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.services.sqs.SqsClient;

@Configuration
@Profile("!test")
public class SqsConsumerConfig {

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
}
