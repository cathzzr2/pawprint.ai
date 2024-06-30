package org.abx.virturalpet.service;

import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.s3.S3Client;

public class S3ServiceTest {
    private static final String BUCKET_NAME = "b-" + UUID.randomUUID();
    private static final String OBJECT_KEY = "k-" + UUID.randomUUID();
    private static final S3Client s3Client = S3Client.create();

    @Test
    void testCreatePresignedGetUrl() {
        S3Service presignInstanceUnderTest = new S3Service(s3Client);

        final String presignedUrlString = presignInstanceUnderTest.generatePresignedUrl(BUCKET_NAME, OBJECT_KEY);
        Assertions.assertNotNull(presignedUrlString);
        Assertions.assertTrue(presignedUrlString.contains(OBJECT_KEY));
    }
}
