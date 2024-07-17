package org.abx.virturalpet.service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.abx.virturalpet.dto.ImmutableUploadServiceDto;
import org.abx.virturalpet.dto.UploadServiceDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.S3Object;

public class UploadListObjectsServiceTest {

    private final S3Client s3Client = Mockito.mock(S3Client.class);
    private final UploadListObjectsService uploadListObjectsService = new UploadListObjectsService(s3Client);

    @Test
    public void testListObject_ok() {
        S3Object s3Object = S3Object.builder().key("test-key/test-file.txt").build();
        ListObjectsV2Response listObjectsV2Response =
                ListObjectsV2Response.builder().contents(s3Object).build();
        HeadObjectResponse headObjectResponse =
                HeadObjectResponse.builder().metadata(Map.of("key1", "value1")).build();

        Mockito.when(s3Client.listObjectsV2(Mockito.any(ListObjectsV2Request.class)))
                .thenReturn(listObjectsV2Response);
        Mockito.when(s3Client.headObject(Mockito.any(HeadObjectRequest.class))).thenReturn(headObjectResponse);

        List<UploadServiceDto> actualResults = uploadListObjectsService.listObjects("test-bucket", "test-prefix");

        Assertions.assertNotNull(actualResults, "Actual result should not be null");

        UploadServiceDto expectedResult = ImmutableUploadServiceDto.builder()
                .s3Key("test-key/test-file.txt")
                .fileName("test-file.txt")
                .userId("")
                .photoId(UUID.randomUUID().toString())
                .timestamp(LocalDateTime.now().toString())
                .metadata("{key1=value1}")
                .build();

        Assertions.assertEquals(1, actualResults.size());
        Assertions.assertEquals(expectedResult.getS3Key(), actualResults.get(0).getS3Key());
        Assertions.assertEquals(
                expectedResult.getFileName(), actualResults.get(0).getFileName());
        Assertions.assertEquals(
                expectedResult.getMetadata(), actualResults.get(0).getMetadata());
    }

    @Test
    public void testListObject_notFound() {
        ListObjectsV2Response listObjectsV2Response = ListObjectsV2Response.builder()
                .contents(Collections.emptyList())
                .build();

        Mockito.when(s3Client.listObjectsV2(Mockito.any(ListObjectsV2Request.class)))
                .thenReturn(listObjectsV2Response);

        List<UploadServiceDto> actualResults = uploadListObjectsService.listObjects("test-bucket", "test-prefix");

        Assertions.assertTrue(actualResults.isEmpty());
    }

    @Test
    public void testListObjectsWithPagination_ok() {
        S3Object s3Object = S3Object.builder().key("test-key/test-file.txt").build();
        ListObjectsV2Response listObjectsV2Response =
                ListObjectsV2Response.builder().contents(s3Object).build();
        HeadObjectResponse headObjectResponse =
                HeadObjectResponse.builder().metadata(Map.of("key1", "value1")).build();

        Mockito.when(s3Client.listObjectsV2(Mockito.any(ListObjectsV2Request.class)))
                .thenReturn(listObjectsV2Response);
        Mockito.when(s3Client.headObject(Mockito.any(HeadObjectRequest.class))).thenReturn(headObjectResponse);

        UploadServiceDto expectedResult = ImmutableUploadServiceDto.builder()
                .s3Key("test-key/test-file.txt")
                .fileName("test-file.txt")
                .userId("")
                .photoId(UUID.randomUUID().toString()) // Replace with actual value
                .timestamp(LocalDateTime.now().toString())
                .metadata("{key1=value1}")
                .build();

        List<UploadServiceDto> actualResults =
                uploadListObjectsService.listObjectsWithPagination("test-bucket", "test-prefix", 0, 10);

        Assertions.assertEquals(1, actualResults.size());
        Assertions.assertEquals(expectedResult.getS3Key(), actualResults.get(0).getS3Key());
        Assertions.assertEquals(
                expectedResult.getFileName(), actualResults.get(0).getFileName());
        Assertions.assertEquals(
                expectedResult.getMetadata(), actualResults.get(0).getMetadata());
    }
}
