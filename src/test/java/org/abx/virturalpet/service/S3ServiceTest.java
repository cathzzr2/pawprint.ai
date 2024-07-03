package org.abx.virturalpet.service;

import java.net.URI;

import com.adobe.testing.s3mock.testcontainers.S3MockContainer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.utils.AttributeMap;
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient;

import static software.amazon.awssdk.http.SdkHttpConfigurationOption.TRUST_ALL_CERTIFICATES;

@Testcontainers
public class S3ServiceTest {
    @Container
    private S3MockContainer s3Mock = new S3MockContainer("latest");
    private S3Client s3Client;
    private static final String TEST_BUCKET_NAME = "test-bucket";
    private static final String TEST_OBJECT_KEY = "test-object";

    @BeforeEach
    void beforeEach() {
        // add an endpoint
        var endpoint = s3Mock.getHttpsEndpoint();
        var serviceConfig = S3Configuration.builder()
                .pathStyleAccessEnabled(true)
                .build();
        var httpClient = UrlConnectionHttpClient.builder()
                .buildWithDefaults(AttributeMap.builder()
                        .put(TRUST_ALL_CERTIFICATES, Boolean.TRUE)
                        .build());
        s3Client.createBucket(CreateBucketRequest.builder().bucket(TEST_BUCKET_NAME).build());
    }

    @Test
    void testCreatePresignedGetUrl() {
        var s3Service = new S3Service(s3Client);
        s3Service.generatePresignedUrl(TEST_BUCKET_NAME, TEST_OBJECT_KEY);


    }
}
