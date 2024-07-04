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

    // create your own message processor for different tasks
    @Bean
    public GenImageSqsMessageProcessor genImageSqsMessageProcessor() {
        return new GenImageSqsMessageProcessor();
    }

    // feed in different message processor to process different job
    @Bean
    @Qualifier("ImageJobSqsConsumer")
    public SqsConsumer sqsConsumer(
            SqsClient sqsClient,
            @Qualifier("ImageJobSqsQueueName") String queueUrl,
            MessageProcessor messageProcessor) {
        return new SqsConsumer(sqsClient, queueUrl, messageProcessor);
    }
}
