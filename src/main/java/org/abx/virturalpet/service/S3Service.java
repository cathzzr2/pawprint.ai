package org.abx.virturalpet.service;

import java.io.File;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.abx.virturalpet.exception.S3DeletionException;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.Delete;
import software.amazon.awssdk.services.s3.model.DeleteObjectsRequest;
import software.amazon.awssdk.services.s3.model.ObjectIdentifier;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

@Service
public class S3Service {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;
    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(S3Service.class);

    @Autowired
    public S3Service(S3Client s3Client, S3Presigner s3Presigner) {
        this.s3Client = s3Client;
        this.s3Presigner = s3Presigner;
    }

    // TODO: Implement this method
    // reference:
    // https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/s3/src/main/java/com/example/s3/GeneratePresignedUrlAndUploadObject.java
    // https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/s3/src/main/java/com/example/s3/GeneratePresignedUrlAndPutFileWithMetadata.java
    // https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/s3/src/main/java/com/example/s3/GeneratePresignedGetUrlAndRetrieve.java
    public class GeneratePresignedUrlException extends RuntimeException {
        public GeneratePresignedUrlException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public String generatePresignedUrl(String bucketName, String objectKey) {
        try {
            PutObjectRequest objectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(objectKey)
                    .contentType("text/plain")
                    .build();
            PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(10)) // The URL will expire in 10 minutes.
                    .putObjectRequest(objectRequest)
                    .build();
            PresignedPutObjectRequest presignedRequest = s3Presigner.presignPutObject(presignRequest);
            String myUrl = presignedRequest.url().toString();
            logger.info("Presigned URL to upload to: [{}]", myUrl);
            logger.info(
                    "Which HTTP method needs to be used when uploading: [{}]",
                    presignedRequest.httpRequest().method());
            return myUrl;
        } catch (S3Exception e) {
            logger.error("Failed to generate presigned URL", e);
            throw new GeneratePresignedUrlException(
                    "Failed to generate presigned URL for bucket " + bucketName + " and object " + objectKey, e);
        }
    }

    // reference:
    // https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/s3/src/main/java/com/example/s3/PutObject.java
    public class S3UploadException extends Exception {
        public S3UploadException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public void uploadObject(String bucketName, String objectKey, String filePath) throws S3UploadException {
        try {
            Map<String, String> metadata = new HashMap<>();
            metadata.put("x-amz-meta-myVal", "test");
            PutObjectRequest putOb = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(objectKey)
                    .metadata(metadata)
                    .build();

            s3Client.putObject(putOb, RequestBody.fromFile(new File(filePath)));
            logger.info("Successfully placed {} into bucket {}", objectKey, bucketName);
        } catch (S3Exception e) {
            logger.error("Failed to upload {} to bucket {}", objectKey, bucketName, e);
            throw new S3UploadException("Failed to upload to S3", e);
        }
    }

    // reference:
    // https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/s3/src/main/java/com/example/s3/GetObjectData.java
    public Object getObject(String bucketName, String objectKey) {
        return null;
    }

    // reference:
    // https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/s3/src/main/java/com/example/s3/DeleteObjects.java
    // https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/s3/src/main/java/com/example/s3/DeleteMultiObjects.java
    public void deleteObject(String bucketName, String objectKey) {
        ObjectIdentifier toDelete = ObjectIdentifier.builder().key(objectKey).build();

        try {
            DeleteObjectsRequest dor = DeleteObjectsRequest.builder()
                    .bucket(bucketName)
                    .delete(Delete.builder().objects(toDelete).build())
                    .build();

            s3Client.deleteObjects(dor);

        } catch (S3Exception e) {
            logger.error(
                    "S3Exception while deleting object: {}", e.awsErrorDetails().errorMessage());
            throw new S3DeletionException(e.awsErrorDetails().errorMessage());
        }
    }

    public void deleteObjects(String bucketName, String... objectKeys) {
        List<ObjectIdentifier> toDelete = new ArrayList<>();
        for (String objectKey : objectKeys) {
            toDelete.add(ObjectIdentifier.builder().key(objectKey).build());
        }

        try {
            DeleteObjectsRequest dor = DeleteObjectsRequest.builder()
                    .bucket(bucketName)
                    .delete(Delete.builder().objects(toDelete).build())
                    .build();

            s3Client.deleteObjects(dor);

        } catch (S3Exception e) {
            logger.error(
                    "S3Exception while deleting objects: {}",
                    e.awsErrorDetails().errorMessage());
            throw new S3DeletionException(e.awsErrorDetails().errorMessage());
        }
    }

    // https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/s3/src/main/java/com/example/s3/ListObjects.java
    // https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/s3/src/main/java/com/example/s3/ListObjectsPaginated.java
    public List<String> listObjects(String bucketName, String prefix) {
        return new ArrayList<>();
    }

    public List<String> listObjectsWithPagination(String bucketName, String prefix, int offset, int limit) {
        return new ArrayList<>();
    }
}
