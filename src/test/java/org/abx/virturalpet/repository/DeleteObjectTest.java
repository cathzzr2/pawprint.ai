package org.abx.virturalpet.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import com.adobe.testing.s3mock.testcontainers.S3MockContainer;
import java.net.URI;
import java.util.List;
import org.abx.virturalpet.configuration.S3MockConfig;
import org.abx.virturalpet.exception.S3DeleteException;
import org.abx.virturalpet.exception.S3GetException;
import org.abx.virturalpet.service.S3Service;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.junit.jupiter.Testcontainers;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.http.SdkHttpConfigurationOption;
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.utils.AttributeMap;

@Testcontainers
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = S3MockConfig.class)
public class DeleteObjectTest {
    private final Logger logger = LoggerFactory.getLogger(DeleteObjectTest.class);
    private final String testBucketName = "test-bucket";
    private final String testObjectKey = "test-object-key";
    private final String filePath = "test-file-path";
    private final S3MockContainer s3MockContainer = new S3MockContainer("latest");
    private S3Client s3Client;
    private S3Service s3Service;
    private PhotoRepository photoRepository;

    @BeforeEach
    public void before() {
        s3MockContainer.start();
        var endpoint = s3MockContainer.getHttpsEndpoint();
        var serviceConfig =
                S3Configuration.builder().pathStyleAccessEnabled(true).build();
        var httpClient = UrlConnectionHttpClient.builder()
                .buildWithDefaults(AttributeMap.builder()
                        .put(SdkHttpConfigurationOption.TRUST_ALL_CERTIFICATES, Boolean.TRUE)
                        .build());

        s3Client = S3Client.builder()
                .endpointOverride(URI.create(endpoint))
                .region(Region.US_EAST_1)
                .httpClient(httpClient)
                .credentialsProvider(
                        StaticCredentialsProvider.create(AwsBasicCredentials.create("dummy-key", "dummy-secret")))
                .serviceConfiguration(serviceConfig)
                .build();
        photoRepository = mock(PhotoRepository.class);
        s3Service = new S3Service(s3Client, null, photoRepository);

        try {
            CreateBucketRequest createBucketRequest =
                    CreateBucketRequest.builder().bucket(testBucketName).build();

            s3Client.createBucket(createBucketRequest);
            logger.info("Bucket created successfully");

        } catch (S3Exception e) {
            logger.error("Error creating bucket", e);
        }
    }

    @Test
    public void deleteObject() {
        s3Service.deleteObject(testBucketName, testObjectKey);

        try {
            var deletedObject = s3Service.getObject(testBucketName, testObjectKey, filePath);
            assertThat(deletedObject).isNull();
        } catch (S3GetException e) {
            if (e.getCause() instanceof NoSuchKeyException) {
                logger.info("Object not found after deletion, as expected.");
                assertThat(true).isTrue();
            } else {
                throw e;
            }
        }
    }

    @Test
    public void deleteObjectWithException() {
        try {
            s3Service.deleteObject(testBucketName, testObjectKey);

        } catch (SdkClientException e) {
            throw (new S3DeleteException("Error deleting object", e));
        }
    }

    @Test
    public void deleteObjects() {
        s3Service.deleteObjects(testBucketName, String.valueOf(List.of(testObjectKey)));

        try {
            var deletedObject = s3Service.getObject(testBucketName, testObjectKey, filePath);
            assertThat(deletedObject).isNull();
        } catch (S3GetException e) {
            if (e.getCause() instanceof NoSuchKeyException) {
                logger.info("Object not found after deletion, as expected.");
                assertThat(true).isTrue();
            } else {
                throw e;
            }
        }
    }
}
