package org.abx.virturalpet.service;

import com.adobe.testing.s3mock.testcontainers.S3MockContainer;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import org.abx.virturalpet.configuration.S3ClientConfig;
import org.abx.virturalpet.exception.S3GetException;
import org.abx.virturalpet.service.S3Service.S3UploadException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.http.SdkHttpConfigurationOption;
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.utils.AttributeMap;

@Testcontainers
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = S3ClientConfig.class)
public class S3ServiceTest {
    private static final Logger logger = LoggerFactory.getLogger(S3ServiceTest.class);

    @Container
    private S3MockContainer s3Mock = new S3MockContainer("latest");

    private S3Client s3Client;
    private S3Service s3Service;
    private S3Presigner s3Presigner;
    private static final String TEST_BUCKET_NAME = "test-bucket";
    private static final String TEST_OBJECT_KEY = "test-object";

    @BeforeEach
    void beforeEach() {
        s3Mock.start();
        var endpoint = s3Mock.getHttpsEndpoint();
        var serviceConfig =
                S3Configuration.builder().pathStyleAccessEnabled(true).build();
        var httpClient = UrlConnectionHttpClient.builder()
                .buildWithDefaults(AttributeMap.builder()
                        .put(SdkHttpConfigurationOption.TRUST_ALL_CERTIFICATES, Boolean.TRUE)
                        .build());

        // Provide credentials for the S3 client
        var credentialsProvider =
                StaticCredentialsProvider.create(AwsBasicCredentials.create("accessKeyId", "secretAccessKey"));

        s3Client = S3Client.builder()
                .endpointOverride(URI.create(endpoint))
                .region(Region.US_EAST_1)
                .httpClient(httpClient)
                .credentialsProvider(credentialsProvider)
                .serviceConfiguration(serviceConfig)
                .build();

        s3Presigner = S3Presigner.builder()
                .endpointOverride(URI.create(endpoint))
                .region(Region.US_EAST_1)
                .credentialsProvider(credentialsProvider)
                .serviceConfiguration(serviceConfig)
                .build();

        s3Service = new S3Service(s3Client, s3Presigner);

        s3Client.createBucket(
                CreateBucketRequest.builder().bucket(TEST_BUCKET_NAME).build());
    }

    @Test
    public void testCreatePresignedGetUrl() throws Exception {
        // Given
        String bucketName = TEST_BUCKET_NAME;
        String objectKey = TEST_OBJECT_KEY;

        // When
        String presignedUrl = s3Service.generatePresignedUrl(bucketName, objectKey);

        // Then
        if (presignedUrl == null || presignedUrl.isEmpty()) {
            throw new Exception("Generated presigned URL is null or empty");
        }

        Assertions.assertNotNull(presignedUrl);
        logger.info("Generated presigned URL: {}", presignedUrl);
    }

    @Test
    public void testUploadObject() {
        String bucketName = TEST_BUCKET_NAME;
        String objectKey = TEST_OBJECT_KEY;

        // Create a temporary test file
        Path tempFilePath;
        try {
            tempFilePath = Files.createTempFile("test-file", ".txt");
            Files.writeString(tempFilePath, "This is a test file");
        } catch (IOException e) {
            throw new RuntimeException("Failed to create a temporary test file", e);
        }

        // Ensure the test file exists
        Assertions.assertTrue(Files.exists(tempFilePath), "Test file does not exist");

        // Test successful upload
        try {
            s3Service.uploadObject(bucketName, objectKey, tempFilePath.toString());
            logger.info("Successfully uploaded {} to bucket {}", objectKey, bucketName);
        } catch (S3UploadException e) {
            Assertions.fail("Upload should not have failed for a valid file", e);
        }

        // Test upload failure (e.g., invalid bucket)
        try {
            s3Service.uploadObject("invalid-bucket", objectKey, tempFilePath.toString());
            Assertions.fail("Upload should have failed for an invalid bucket");
        } catch (S3UploadException e) {
            Assertions.assertEquals("Failed to upload to S3", e.getMessage());
            logger.info("Correctly failed to upload to an invalid bucket");
        }
    }

    @Test
    public void testGetObject() throws S3UploadException {
        String bucketName = TEST_BUCKET_NAME;
        String objectKey = TEST_OBJECT_KEY;
        String expectedContent = "This is a test file";

        Path tempFilePath;
        try {
            tempFilePath = Files.createTempFile("test-file", ".txt");
            Files.writeString(tempFilePath, expectedContent);
        } catch (IOException e) {
            throw new RuntimeException("Failed to create a temporary test file", e);
        }

        Assertions.assertTrue(Files.exists(tempFilePath), "Test file does not exist");

        s3Service.uploadObject(bucketName, objectKey, tempFilePath.toString());
        logger.info("Successfully uploaded {} to bucket {}", objectKey, bucketName);

        Path downloadedFilePath;
        try {
            downloadedFilePath = Files.createTempFile("downloaded-file", ".txt");
            s3Service.getObject(bucketName, objectKey, downloadedFilePath.toString());

            String downloadedContent = Files.readString(downloadedFilePath);
            Assertions.assertEquals(
                    expectedContent, downloadedContent, "Downloaded content does not match expected content");

            logger.info("Successfully downloaded {} from bucket {}", objectKey, bucketName);
        } catch (IOException e) {
            Assertions.fail("Failed to create a temporary file for download", e);
        } catch (S3GetException e) {
            Assertions.fail("GetObject should not have failed for a valid file", e);
        }
    }
}
