package org.abx.virturalpet.service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Timestamp;
import java.util.UUID;
import org.abx.virturalpet.dto.ImageGenSqsDto;
import org.abx.virturalpet.dto.ImmutableImageGenSqsDto;
import org.abx.virturalpet.dto.ImmutablePhotoGenerationDto;
import org.abx.virturalpet.dto.PhotoGenerationDto;
import org.abx.virturalpet.model.JobProgress;
import org.abx.virturalpet.model.JobResultModel;
import org.abx.virturalpet.model.PhotoJobModel;
import org.abx.virturalpet.repository.JobProgressRepository;
import org.abx.virturalpet.repository.JobResultRepository;
import org.abx.virturalpet.repository.PhotoJobRepository;
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
    private final JobResultRepository jobResultRepository;
    private final PhotoJobRepository photoJobRepository;
    private final JobProgressRepository jobProgressRepository;

    public PhotoGenerationService(
            S3Client s3Client,
            SqsClient sqsClient,
            @Value("${sqs.queue.url}") String queueUrl,
            @Value("${s3.bucket.name}") String bucketName,
            ImageGenSqsProducer imageGenSqsProducer,
            JobResultRepository jobResultRepository,
            PhotoJobRepository photoJobRepository,
            JobProgressRepository jobProgressRepository) {
        this.s3Client = s3Client;
        this.sqsClient = sqsClient;
        this.queueUrl = queueUrl;
        this.bucketName = bucketName;
        this.imageGenSqsProducer = imageGenSqsProducer;
        this.jobResultRepository = jobResultRepository;
        this.photoJobRepository = photoJobRepository;
        this.jobProgressRepository = jobProgressRepository;
    }

    public PhotoGenerationDto generateImg(String imageData, String userIdStr, String jobType) {
        if (imageData == null || imageData.isEmpty()) {
            throw new IllegalArgumentException("Image data cannot be null or empty");
        }

        UUID userId = UUID.fromString(userIdStr);

        // TODO: get s3key from photoRepo when it is available
        String s3Key = "path/to/photo";

        // TODO: get imageId from photoRepo when it is available
        UUID photoId = UUID.randomUUID();
        UUID jobId = UUID.randomUUID();
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        // Save job info in jobRepo
        PhotoJobModel photoJobModel = PhotoJobModel.newBuilder()
                .withJobId(jobId)
                .withPhotoId(photoId)
                .withUserId(userId)
                .withJobType(jobType)
                .withJobSubmissionTime(timestamp)
                .build();
        photoJobRepository.save(photoJobModel);

        // Save job progress in MongoDB
        JobProgress jobProgress = JobProgress.Builder.newBuilder()
                .withJobId(jobId)
                .withJobType(jobType)
                .withJobStatus("in queue")
                .build();
        jobProgressRepository.save(jobProgress);

        // Send message to SQS
        ImageGenSqsDto imageGenSqsDto = ImmutableImageGenSqsDto.builder()
                .jobId(jobId.toString())
                .photoId(photoId.toString())
                .build();
        imageGenSqsProducer.sendMessage(imageGenSqsDto);

        return ImmutablePhotoGenerationDto.builder()
                .imageData(imageData)
                .imageId(photoId.toString())
                .jobId(jobId.toString())
                .userId(userIdStr)
                .jobType(jobType)
                .build();
    }

    public Path fetchPhotoFromS3(String photoId) throws IOException {
        Path tempFile = null;
        try {
            // TODO: get s3Key from photoRepo
            String s3Key = photoId;
            tempFile = Files.createTempFile("s3_", "_" + s3Key);

            // 从 S3 获取对象
            GetObjectRequest getObjectRequest =
                    GetObjectRequest.builder().bucket(bucketName).key(s3Key).build();
            ResponseInputStream<GetObjectResponse> s3Object = s3Client.getObject(getObjectRequest);

            // 将 s3Object 写入 tempFile
            try (InputStream inputStream = s3Object;
                    FileOutputStream outputStream = new FileOutputStream(tempFile.toFile())) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            }

            return tempFile;
        } catch (IOException e) {
            logger.error("Failed to fetch origin photo from S3", e);
            throw new IOException(e);
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

        JobProgress jobProgress = jobProgressRepository.findByJobId(UUID.fromString(jobId));
        if (jobProgress == null) {
            throw new RuntimeException("Job with ID " + jobId + " not found");
        }

        String jobStatus = jobProgress.getJobStatus();
        return ImmutablePhotoGenerationDto.builder().status(jobStatus).build();
    }

    public PhotoGenerationDto getGenImg(String imageId) {
        if (imageId == null || imageId.isEmpty()) {
            throw new IllegalArgumentException("imageId cannot be null or empty");
        }

        // 从 jobResultRepository 获取 jobResultModel
        JobResultModel jobResult = jobResultRepository.findByJobId(UUID.fromString(imageId));
        if (jobResult == null) {
            throw new IllegalArgumentException("Generated photo not found");
        }

        String s3Key = jobResult.getS3Key();
        Path photoPath;
        try {
            photoPath = fetchPhotoFromS3(s3Key);
        } catch (IOException e) {
            throw new RuntimeException("Failed to fetch photo from S3", e);
        }

        return ImmutablePhotoGenerationDto.builder()
                .imageData(photoPath.toString())
                .build();
    }
}
