package org.abx.virturalpet.service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import org.abx.virturalpet.dto.ImageGenSqsDto;
import org.abx.virturalpet.dto.ImmutableImageGenSqsDto;
import org.abx.virturalpet.dto.ImmutablePhotoGenerationDto;
import org.abx.virturalpet.dto.PhotoGenerationDto;
import org.abx.virturalpet.sqs.ImageGenSqsProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.sqs.SqsClient;

@Service
public class PhotoGenerationService {
    private static final Logger logger = LoggerFactory.getLogger(PhotoGenerationService.class);

    private final S3Client s3Client;
    private final SqsClient sqsClient;
    private final String queueUrl;
    private final String bucketName;
    private final ImageGenSqsProducer imageGenSqsProducer;

    public PhotoGenerationService(
            S3Client s3Client,
            SqsClient sqsClient,
            @Value("${sqs.queue.url}") String queueUrl,
            @Value("${s3.bucket.name}") String bucketName,
            ImageGenSqsProducer imageGenSqsProducer) {
        this.s3Client = s3Client;
        this.sqsClient = sqsClient;
        this.queueUrl = queueUrl;
        this.bucketName = bucketName;
        this.imageGenSqsProducer = imageGenSqsProducer;
    }

    public PhotoGenerationDto generateImg(String imageData) {
        if (imageData == null || imageData.isEmpty()) {
            throw new IllegalArgumentException("Image data cannot be null or empty");
        }

        // TODO: get s3key from photoRepo when it is available
        String s3Key = "path/to/photo";
        // TODO; mongoDb to get job type
        //12
        String jobType = "stylized";

        // TODO: get imageId from photoRepo when it is available
        String photoId = UUID.randomUUID().toString();
        String jobId = UUID.randomUUID().toString();
        //asd
        // TODO: save job info in jobRepo
        // jobRepository.save(jobModel);

        // send message to SQS
        ImageGenSqsDto imageGenSqsDto =
                ImmutableImageGenSqsDto.builder().jobId(jobId).photoId(photoId).build();
        imageGenSqsProducer.sendMessage(imageGenSqsDto);

        return ImmutablePhotoGenerationDto.builder()
                .imageData(imageData)
                .imageId(photoId)
                .jobId(jobId)
                .build();
    }

    public String fetchPhotoFromS3(String photoId) throws IOException {
        Path tempFile = null;
        try {
            // TODO: get s3Key from photoRepo
            String s3Key = photoId;
            tempFile = Files.createTempFile("s3_", "_" + s3Key);

            // fetch from S3
            GetObjectRequest getObjectRequest =
                    GetObjectRequest.builder().bucket(bucketName).key(s3Key).build();
            ResponseInputStream<GetObjectResponse> s3Object = s3Client.getObject(getObjectRequest);

            // write s3Object to tempFile
            try (InputStream inputStream = s3Object;
                 FileOutputStream outputStream = new FileOutputStream(tempFile.toFile())) {
                byte[] buffer = new byte[1024];
                int byteRead;
                while ((byteRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, byteRead);
                }
            }
            // read tempFile into a String
            byte[] fileData = Files.readAllBytes(tempFile);
            return new String(fileData, StandardCharsets.UTF_8);
        } catch (IOException e) {
            logger.error("failed to fetch origin photo from s3", e);
            throw new IOException(e); // why not throw e?
        } finally {
            if (tempFile != null) {
                try {
                    Files.deleteIfExists(tempFile);
                } catch (IOException e) {
                    logger.error("Failed to delete tempFile", e);
                }
            }
        }
    }

    public String callExternalApi(String jobType, String jobId, String photoData) {
        // Mock implementation of an external API call
        logger.info("Calling external API with jobType: {}, jobId: {}, photoData: {}", jobType, jobId, photoData);
        // Mock API response
        return "Success";
    }

    public PhotoGenerationDto checkJobStatus(String jobId) {
        if (jobId == null || jobId.isEmpty()) {
            throw new IllegalArgumentException("jobId cannot be null or empty");
        }
        // TODO: get job status from jobProgressRepo(MongoDb)
        String jobStatus = "in queue";

        return ImmutablePhotoGenerationDto.builder().status(jobStatus).build();
    }

    public PhotoGenerationDto getGenImg(String imageId) {
        if (imageId == null || imageId.isEmpty()) {
            throw new IllegalArgumentException("imageId cannot be null or empty");
        }
        // TODO: get photoData from resultRepo
        String genPhoto = "base64_encoded_photo";
        return ImmutablePhotoGenerationDto.builder().imageData(genPhoto).build();
    }
}
