package org.abx.virturalpet.configuration;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@TestConfiguration
public class TestS3Configuration {
    @Bean
    public S3Client s3Client() {

        return S3Client.builder()
                .credentialsProvider(() -> null)
                .region(Region.US_EAST_1).build();
    }
}
