package org.abx.virturalpet.configuration;

import java.net.URI;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;

@Configuration
public class SqsClientConfig {

    @Bean(destroyMethod = "close")
    public SqsClient sqsClient() {
        // "http://localhost:4566/000000000000/virtual-pet-queue"

        return SqsClient.builder()
                .endpointOverride(URI.create("http://localhost:4566/"))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create("fakeMyKeyId", "fakeSecretAccessKey")))
                .region(Region.US_EAST_1)
                .build();
    }

    @Bean
    @Qualifier("ImageJobSqsQueueName")
    public String imageJobSqsQueueName() {
        return "virtual-pet-queue";
    }
}
