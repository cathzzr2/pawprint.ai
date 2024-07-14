package org.abx.virturalpet.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.abx.virturalpet.dto.ImmutableUploadServiceDto;
import org.abx.virturalpet.dto.UploadServiceDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.S3Exception;

@Service
public class UploadListObjectsService {
    private static final Logger logger = LoggerFactory.getLogger(UploadListObjectsService.class);
    private final S3Client s3Client;

    @Autowired
    public UploadListObjectsService(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    public List<UploadServiceDto> listObjects(String bucketName, String prefix) {
        ListObjectsV2Request listObjectsV2Request =
                ListObjectsV2Request.builder().bucket(bucketName).prefix(prefix).build();

        ListObjectsV2Response listObjectsV2Response;
        try {
            listObjectsV2Response = s3Client.listObjectsV2(listObjectsV2Request);
        } catch (S3Exception e) {
            logger.error("Error listing objects in bucket {} with prefix {}", bucketName, prefix, e);
            throw new RuntimeException(
                    String.format("Error listing objects in bucket %s with prefix %s", bucketName, prefix), e);
        }

        return listObjectsV2Response.contents().stream()
                .map(s3Object -> {
                    String key = s3Object.key();
                    String fileName = extractFileNameFromKey(key);
                    Map<String, String> metadata = extractObjectMetadata(bucketName, key);
                    return ImmutableUploadServiceDto.builder()
                            .s3Key(key)
                            .fileName(fileName)
                            .userId("") // repalce with photoModel.getUserId()
                            //                            .photoId(UUID.randomUUID().toString()) // repalce with
                            // photoModel.getPhotoID()
                            .timestamp(LocalDateTime.now().toString()) // repalce with photoModel.getUploadTime()
                            .metadata(metadata.toString())
                            .build();
                })
                .collect(Collectors.toList());
    }

    public List<UploadServiceDto> listObjectsWithPagination(String bucketName, String prefix, int offset, int limit) {
        ListObjectsV2Request listObjectsV2Request = ListObjectsV2Request.builder()
                .bucket(bucketName)
                .prefix(prefix)
                .maxKeys(limit)
                .build();

        ListObjectsV2Response listObjectsV2Response;
        try {
            listObjectsV2Response = s3Client.listObjectsV2(listObjectsV2Request);
        } catch (S3Exception e) {
            logger.error("Error listing objects in bucket {} with prefix {}", bucketName, prefix, e);
            throw new RuntimeException(
                    String.format("Error listing objects in bucket %s with prefix %s", bucketName, prefix), e);
        }

        return listObjectsV2Response.contents().stream()
                .skip(offset)
                .map(s3Object -> {
                    String key = s3Object.key();
                    String fileName = extractFileNameFromKey(key);
                    Map<String, String> metadata = extractObjectMetadata(bucketName, key);
                    return ImmutableUploadServiceDto.builder()
                            .s3Key(key)
                            .fileName(fileName)
                            .userId("") // repalce with photoModel.getUserId()
                            //                            .photoId(UUID.randomUUID().toString()) // repalce with
                            // photoModel.getPhotoID()
                            .timestamp(LocalDateTime.now().toString()) // repalce with photoModel.getUploadTime()
                            .metadata(metadata.toString())
                            .build();
                })
                .collect(Collectors.toList());
    }

    // extract filename from s3 key
    private String extractFileNameFromKey(String key) {
        // assume filename is the last part
        return key.substring(key.lastIndexOf('/') + 1);
    }

    // extract metadata from s3 key
    private Map<String, String> extractObjectMetadata(String bucketName, String key) {
        try {
            HeadObjectRequest headObjectRequest =
                    HeadObjectRequest.builder().bucket(bucketName).key(key).build();

            HeadObjectResponse headObjectResponse = s3Client.headObject(headObjectRequest);
            return headObjectResponse.metadata();
        } catch (S3Exception e) {
            logger.error("Error getting metadata for object {} in bucket {}", key, bucketName, e);
            throw new RuntimeException(
                    String.format("Error getting metadata for object {} in bucket {}", key, bucketName, e));
        }
    }
}
