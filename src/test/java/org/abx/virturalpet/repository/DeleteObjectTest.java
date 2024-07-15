package org.abx.virturalpet.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static software.amazon.awssdk.http.SdkHttpConfigurationOption.TRUST_ALL_CERTIFICATES;

import com.adobe.testing.s3mock.testcontainers.S3MockContainer;
import java.net.URI;
import java.util.List;

import org.abx.virturalpet.service.S3Service;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Testcontainers;
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.utils.AttributeMap;

@Testcontainers
public class DeleteObjectTest {
    private final String TEST_BUCKET_NAME = "test-bucket";
    private final String TEST_OBJECT_KEY = "test-object-key";
    private S3MockContainer s3MockContainer = new S3MockContainer("latest");
    private S3Client s3Client;
    private S3Service s3Service;

    @BeforeEach
    public void before() {
        s3MockContainer.start();
        var endpoint = s3MockContainer.getHttpsEndpoint();
        var serviceConfig =
                S3Configuration.builder().pathStyleAccessEnabled(true).build();
        var httpClient = UrlConnectionHttpClient.builder()
                .buildWithDefaults(AttributeMap.builder()
                        .put(TRUST_ALL_CERTIFICATES, Boolean.TRUE)
                        .build());
        s3Client = S3Client.builder()
                .endpointOverride(URI.create(endpoint))
                .serviceConfiguration(serviceConfig)
                .httpClient(httpClient)
                .build();
        s3Service = new S3Service(s3Client);
    }

    @Test
    public void deleteObject() {

        s3Service.deleteObject(TEST_BUCKET_NAME, TEST_OBJECT_KEY);
        var deletedObject = s3Service.getObject(TEST_BUCKET_NAME, TEST_OBJECT_KEY);
        assertThat(deletedObject).isNull();
    }

    @Test
    public void deleteObjectWithException() {
        s3MockContainer.stop();
        try {
            s3Service.deleteObject(TEST_BUCKET_NAME, TEST_OBJECT_KEY);
        } catch (Exception e) {
            assertThat(e).isInstanceOf(RuntimeException.class);
        }
    }

    @Test
    public void deleteObjects() {
        s3Service.deleteObjects(TEST_BUCKET_NAME, String.valueOf(List.of(TEST_OBJECT_KEY)));
        var deletedObject = s3Service.getObject(TEST_BUCKET_NAME, TEST_OBJECT_KEY);
        assertThat(deletedObject).isNull();
    }
}
