package org.abx.virturalpet.configuration;

import org.abx.virturalpet.repository.JobProgressRepository;
import org.abx.virturalpet.repository.JobResultRepository;
import org.abx.virturalpet.repository.PhotoJobRepository;
import org.abx.virturalpet.repository.PhotoRepository;
import org.abx.virturalpet.service.PhotoGenerationService;
import org.abx.virturalpet.sqs.GenImageSqsMessageProcessor;
import org.abx.virturalpet.sqs.MessageProcessor;
import org.abx.virturalpet.sqs.SqsConsumer;
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
    public GenImageSqsMessageProcessor genImageSqsMessageProcessor(
            PhotoGenerationService photoGenerationService,
            JobProgressRepository jobRepository,
            PhotoJobRepository photoJobRepository,
            JobResultRepository resultRepository,
            PhotoRepository photoRepository) {
        return new GenImageSqsMessageProcessor(
                photoGenerationService, jobRepository, photoJobRepository, resultRepository, photoRepository);
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
