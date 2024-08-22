package org.abx.virturalpet.service;

import java.io.File;
import java.io.FileOutputStream;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import org.abx.virturalpet.dto.ImmutableUploadServiceDto;
import org.abx.virturalpet.dto.UploadServiceDto;
import org.abx.virturalpet.model.PhotoModel;
import org.abx.virturalpet.repository.PhotoRepository;
import org.abx.virturalpet.service.S3Service.S3UploadException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Service
public class UploadService {
    public static final String MEDIA_UPLOAD_SUCCESS = "media upload success";
    private static final String MEDIA_UPLOAD_FAILED = "media upload failed";
    public static final String MEDIA_NOT_PROVIDED = "media not provided";
    public static final Logger logger = LoggerFactory.getLogger(UploadService.class);

    private final S3Service s3Service;
    private final String bucketName;
    private final PhotoRepository photoRepository;
    private final S3Client s3Client;
    private final GenerativeAiService generativeAiService;
    private final S3Presigner s3Presigner;

    public UploadService(
            S3Service s3Service,
            S3Presigner s3Presigner,
            @Value("${s3.bucket.name}") String bucketName,
            PhotoRepository photoRepository,
            S3Client s3Client,
            GenerativeAiService generativeAiService) {
        this.s3Presigner = s3Presigner;
        this.s3Service = s3Service;
        this.bucketName = bucketName;
        this.photoRepository = photoRepository;
        this.s3Client = s3Client;
        this.generativeAiService = generativeAiService;
    }

    public String generatePresignedUrl(String bucketName, String objectKey) {
        return s3Service.generatePresignedUrl(bucketName, objectKey);
    }

    public void uploadObject(String bucketName, String objectKey, String filePath)
            throws S3UploadException, S3UploadException {
        s3Service.uploadObject(bucketName, objectKey, filePath);
    }

    public UploadServiceDto uploadMediaRequest(String photoId, String fileName, byte[] fileData)
            throws S3UploadException {
        if (fileName == null || fileName.isEmpty() || fileData == null || fileData.length == 0) {
            return ImmutableUploadServiceDto.builder()
                    .statusMsg(MEDIA_NOT_PROVIDED)
                    .fileName("")
                    .userId("")
                    .timestamp("")
                    .metadata("")
                    .build();
        }

        PhotoModel photoModel = fetchPhotoModel(UUID.fromString(photoId));
        UUID userId = photoModel.getUserId();

        String s3Key = uploadFile(fileName, fileData);
        String metadata = "";
        if (!s3Key.isEmpty()) {
            metadata = extractObjectMetadata(bucketName, s3Key).toString(); // get metadata
        }

        if (!s3Key.isEmpty()) {
            return ImmutableUploadServiceDto.builder()
                    .fileName(fileName)
                    .userId(String.valueOf(userId))
                    .statusMsg(MEDIA_UPLOAD_SUCCESS)
                    .timestamp(Instant.now().toString())
                    .s3Key(s3Key)
                    .metadata(metadata)
                    .build();
        } else {
            return ImmutableUploadServiceDto.builder()
                    .fileName(fileName)
                    .userId(String.valueOf(userId))
                    .statusMsg(MEDIA_UPLOAD_FAILED)
                    .timestamp(Instant.now().toString())
                    .metadata(metadata)
                    .build();
        }
    }

    public PhotoModel fetchPhotoModel(UUID photoId) {
        return photoRepository
                .findByPhotoId(photoId)
                .orElseThrow(() -> new RuntimeException("Photo not found with id: " + photoId));
    }

    public String uploadFile(String fileName, byte[] fileData) throws S3UploadException {
        File tempFile = null;
        try {
            // create temp file
            tempFile = File.createTempFile("upload_", null);
            // write byte array to temp file
            try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                fos.write(fileData);
            }

            String objectKey = "uploads/" + fileName;
            s3Service.uploadObject(bucketName, objectKey, tempFile.getAbsolutePath());
            return objectKey;
        } catch (Exception e) {
            throw new S3UploadException("Failed to upload to S3", e);
        } finally {
            if (tempFile != null && tempFile.exists()) {
                tempFile.delete(); // delete temp file
            }
        }
    }

    // extract metadata from s3 key
    private Map<String, String> extractObjectMetadata(String bucketName, String key) {
        try {
            HeadObjectRequest headObjectRequest =
                    HeadObjectRequest.builder().bucket(bucketName).key(key).build();
            HeadObjectResponse headObjectResponse = s3Client.headObject(headObjectRequest);

            if (headObjectResponse == null) {
                logger.warn("No metadata found for object {} in bucket {}", key, bucketName);
                return Map.of(); // return empty map
            }

            return headObjectResponse.metadata();
        } catch (S3Exception e) {
            logger.error("Error getting metadata for object {} in bucket {}", key, bucketName, e);
            return Map.of(); // return empty map
        }
    }
}
