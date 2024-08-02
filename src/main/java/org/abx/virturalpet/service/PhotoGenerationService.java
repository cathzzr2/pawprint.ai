package org.abx.virturalpet.service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Timestamp;
import java.util.Optional;
import java.util.UUID;
import org.abx.virturalpet.dto.ImageGenSqsDto;
import org.abx.virturalpet.dto.ImmutableImageGenSqsDto;
import org.abx.virturalpet.dto.ImmutablePhotoGenerationDto;
import org.abx.virturalpet.dto.JobStatus;
import org.abx.virturalpet.dto.JobType;
import org.abx.virturalpet.dto.PhotoGenerationDto;
import org.abx.virturalpet.model.JobProgress;
import org.abx.virturalpet.model.JobResultModel;
import org.abx.virturalpet.model.PhotoJobModel;
import org.abx.virturalpet.model.PhotoModel;
import org.abx.virturalpet.repository.JobProgressRepository;
import org.abx.virturalpet.repository.JobResultRepository;
import org.abx.virturalpet.repository.PhotoJobRepository;
import org.abx.virturalpet.repository.PhotoRepository;
import org.abx.virturalpet.sqs.ImageGenSqsProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.image.ImageGeneration;
import org.springframework.ai.image.ImageResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

@Service
public class PhotoGenerationService {
    private static final Logger logger = LoggerFactory.getLogger(PhotoGenerationService.class);
    private final S3Client s3Client;
    private final String bucketName;
    private final ImageGenSqsProducer imageGenSqsProducer;
    private final JobResultRepository jobResultRepository;
    private final PhotoJobRepository photoJobRepository;
    private final JobProgressRepository jobProgressRepository;
    private final PhotoRepository photoRepository;
    private final GenerativeAiService generativeAiService;
    private final S3Service s3Service;

    public PhotoGenerationService(
            S3Client s3Client,
            @Value("${s3.bucket.name}") String bucketName,
            ImageGenSqsProducer imageGenSqsProducer,
            JobResultRepository jobResultRepository,
            PhotoJobRepository photoJobRepository,
            JobProgressRepository jobProgressRepository,
            PhotoRepository photoRepository,
            GenerativeAiService generativeAiService,
            S3Service s3Service) {
        this.s3Client = s3Client;
        this.bucketName = bucketName;
        this.photoRepository = photoRepository;
        this.imageGenSqsProducer = imageGenSqsProducer;
        this.jobResultRepository = jobResultRepository;
        this.photoJobRepository = photoJobRepository;
        this.jobProgressRepository = jobProgressRepository;
        this.generativeAiService = generativeAiService;
        this.s3Service = s3Service;
    }

    public PhotoGenerationDto generateImg(String imageData, String photoIdStr, JobType jobType) {
        if (imageData == null || imageData.isEmpty()) {
            throw new IllegalArgumentException("Image data cannot be null or empty");
        }

        // get photoModel from photoRepo
        UUID photoId = UUID.fromString(photoIdStr);
        PhotoModel photoModel = fetchPhotoModel(photoId);

        // Save job info in jobRepo
        UUID userId = photoModel.getUserId(); // get userId from photoModel
        String userIdStr = userId.toString();
        UUID jobId = UUID.randomUUID();
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        PhotoJobModel photoJobModel = PhotoJobModel.newBuilder()
                .withJobId(jobId)
                .withPhotoId(photoId)
                .withUserId(userId)
                .withJobType(jobType.name())
                .withJobSubmissionTime(timestamp)
                .build();
        photoJobRepository.save(photoJobModel);

        // Save job progress in MongoDB
        JobProgress jobProgress = JobProgress.Builder.newBuilder()
                .withJobId(jobId)
                .withJobType(jobType.name())
                .withJobStatus(JobStatus.IN_QUEUE)
                .build();
        jobProgressRepository.save(jobProgress);

        // Send message to SQS
        ImageGenSqsDto imageGenSqsDto = ImmutableImageGenSqsDto.builder()
                .jobId(jobId.toString())
                .photoId(photoId.toString())
                .jobType(jobType)
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

    private PhotoModel fetchPhotoModel(UUID photoId) {
        Optional<PhotoModel> optionalPhotoModel = photoRepository.findByPhotoId(photoId);
        if (optionalPhotoModel.isEmpty()) {
            throw new RuntimeException("Photo not found for id: " + photoId.toString());
        }
        return optionalPhotoModel.get();
    }

    public Path fetchPhotoFromS3(String s3Key) throws IOException {
        Path tempFile;
        try {
            tempFile = Files.createTempFile("s3_", "_" + s3Key);

            // Fetch object from S3
            GetObjectRequest getObjectRequest =
                    GetObjectRequest.builder().bucket(bucketName).key(s3Key).build();

            // Write s3Object to tempFile
            try (ResponseInputStream<GetObjectResponse> s3Object = s3Client.getObject(getObjectRequest);
                    FileOutputStream outputStream = new FileOutputStream(tempFile.toFile())) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = s3Object.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            }

            return tempFile;
        } catch (IOException e) {
            logger.error("Failed to fetch origin photo from S3", e);
            throw new IOException(e);
        }
    }

    public String callExternalApi(JobType jobType, String jobId, String photoData) {
        // Mock implementation of an external API call
        ImageResponse imageResponse = generativeAiService.generateImage(jobType, photoData);
        String generatedImageUrl;
        ImageGeneration imageGeneration = imageResponse.getResult();
        if (imageGeneration != null) {
            generatedImageUrl = imageGeneration.getOutput().getUrl();
            logger.info("Calling external API with jobType: {}, jobId: {}, photoData: {}", jobType, jobId, photoData);
        } else {
            logger.error("Failed to generate image or no image data returned.");
            throw new RuntimeException("Failed to generate image or no image data returned.");
        }
        String s3Key = uploadImageToS3(generatedImageUrl, jobId);
        return s3Key;
    }

    public String uploadImageToS3(String generatedImageUrl, String jobId) {
        Path tempFile;
        try {
            tempFile = Files.createTempFile("s3_", "_" + jobId + ".jpg");
            // Download the generated image from the URL
            try (InputStream inputStream = new URL(generatedImageUrl).openStream();
                    FileOutputStream fileOutputStream = new FileOutputStream(tempFile.toFile())) {

                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    fileOutputStream.write(buffer, 0, bytesRead);
                }
            }

            // Upload the image to S3
            String s3Key = "images/" + jobId + ".jpg";
            s3Service.uploadObject(bucketName, s3Key, tempFile.toString());
            logger.info("Image successfully uploaded to S3 with key: {}", s3Key);

            // Optionally, delete the temporary file after upload
            Files.delete(tempFile);

            return s3Key;
        } catch (IOException | S3Service.S3UploadException e) {
            logger.error("Error occurred during image upload process", e);
            throw new RuntimeException("Error occurred during image upload process", e);
        }
    }

    public PhotoGenerationDto checkJobStatus(String jobId) {
        if (jobId == null || jobId.isEmpty()) {
            throw new IllegalArgumentException("jobId cannot be null or empty");
        }
        // get job status from mongoDb(jobProgressRepo)
        JobProgress jobProgress = jobProgressRepository.findByJobId(UUID.fromString(jobId));
        if (jobProgress == null) {
            throw new RuntimeException("Job with ID " + jobId + " not found");
        }
        JobStatus jobStatus = jobProgress.getJobStatus();
        return ImmutablePhotoGenerationDto.builder().status(jobStatus).build();
    }

    public PhotoGenerationDto getGenImg(String imageId) {
        if (imageId == null || imageId.isEmpty()) {
            throw new IllegalArgumentException("imageId cannot be null or empty");
        }

        // get jobResult from jobResultRepo
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
