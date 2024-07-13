package org.abx.virturalpet.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.adobe.testing.s3mock.testcontainers.S3MockContainer;
import java.net.URI;
import java.util.List;
import org.abx.virturalpet.exception.S3DeleteException;
import org.abx.virturalpet.service.S3Service;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.junit.jupiter.Testcontainers;
import software.amazon.awssdk.auth.credentials.AnonymousCredentialsProvider;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.http.SdkHttpConfigurationOption;
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.utils.AttributeMap;

@Testcontainers
public class DeleteObjectTest {
    private final String testBucketName = "test-bucket";
    private final String testObjectKey = "test-object-key";
    private S3MockContainer s3MockContainer = new S3MockContainer("latest");
    private S3Client s3Client;
    private S3Service s3Service;
    private Logger logger = LoggerFactory.getLogger(DeleteObjectTest.class);

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
                .credentialsProvider(AnonymousCredentialsProvider.create())
                .endpointOverride(URI.create(endpoint))
                .serviceConfiguration(serviceConfig)
                .httpClient(httpClient)
                .build();
        s3Service = new S3Service(s3Client);

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
        var deletedObject = s3Service.getObject(testBucketName, testObjectKey);
        assertThat(deletedObject).isNull();
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
        var deletedObject = s3Service.getObject(testBucketName, testObjectKey);
        assertThat(deletedObject).isNull();
    }
}
