package org.abx.virturalpet.sqs;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.file.Path;
import java.sql.Timestamp;
import java.util.Optional;
import java.util.UUID;
import org.abx.virturalpet.dto.ImageGenSqsDto;
import org.abx.virturalpet.dto.JobStatus;
import org.abx.virturalpet.dto.JobType;
import org.abx.virturalpet.exception.SqsProducerException;
import org.abx.virturalpet.model.JobProgress;
import org.abx.virturalpet.model.JobResultModel;
import org.abx.virturalpet.model.PhotoJobModel;
import org.abx.virturalpet.model.PhotoModel;
import org.abx.virturalpet.repository.JobProgressRepository;
import org.abx.virturalpet.repository.JobResultRepository;
import org.abx.virturalpet.repository.PhotoJobRepository;
import org.abx.virturalpet.repository.PhotoRepository;
import org.abx.virturalpet.service.PhotoGenerationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sqs.model.Message;

@Component
public class GenImageSqsMessageProcessor implements MessageProcessor {
    private static final Logger logger = LoggerFactory.getLogger(GenImageSqsMessageProcessor.class);

    private final PhotoGenerationService photoGenerationService;
    private final PhotoRepository photoRepository;
    private final JobProgressRepository jobRepository;
    private final JobResultRepository resultRepository;
    private final PhotoJobRepository photoJobRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public GenImageSqsMessageProcessor(
            PhotoGenerationService photoGenerationService,
            JobProgressRepository jobRepository,
            PhotoJobRepository photoJobRepository,
            JobResultRepository resultRepository,
            PhotoRepository photoRepository) {
        this.photoGenerationService = photoGenerationService;
        this.jobRepository = jobRepository;
        this.photoJobRepository = photoJobRepository;
        this.resultRepository = resultRepository;
        this.photoRepository = photoRepository;
    }

    @Override
    public void processMessage(Message message) {
        try {
            String messageBody = message.body();
            ImageGenSqsDto sqsDto = objectMapper.readValue(messageBody, ImageGenSqsDto.class);

            // getS3key from photoRepo
            String photoIdStr = sqsDto.photoId();
            UUID photoId = UUID.fromString(sqsDto.photoId());
            Optional<PhotoModel> optionalPhotoModel = photoRepository.findByPhotoId(photoId);
            if (optionalPhotoModel.isEmpty()) {
                throw new RuntimeException("Photo not found for id: " + photoIdStr);
            }

            // Fetch photoData from s3Key in photoRepo
            Path photoData = photoGenerationService.fetchPhotoFromS3(sqsDto.photoId());
            String jobId = sqsDto.getJobId();
            JobType jobType = sqsDto.getJobType();

            // Call external API
            String apiResponse = photoGenerationService.callExternalApi(jobType, jobId, photoData.toString());

            // Fetch job info from PhotoJobRepo;
            PhotoJobModel photoJobModel = photoJobRepository.findByPhotoId(photoId);
            if (photoJobModel == null) {
                throw new SqsProducerException("Job not found for Photo ID " + photoIdStr);
            }

            UUID userId = photoJobModel.getUserId();

            // Save API response in result repository
            JobResultModel jobResultModel = new JobResultModel.Builder()
                    .withResultId(UUID.randomUUID())
                    .withJobId(UUID.fromString(jobId))
                    .withUserId(userId)
                    .withGeneratedTime(new Timestamp(System.currentTimeMillis()))
                    .withS3Key(apiResponse) // API response
                    .build();

            resultRepository.save(jobResultModel);

            // Update job status
            updateJobStatus(jobId, JobStatus.COMPLETED);
        } catch (Exception e) {
            logger.error("Error processing message: {}", message.body(), e);
            throw new SqsProducerException("Failed to process message and update job status", e);
        }
    }

    public void updateJobStatus(String jobId, JobStatus status) {
        JobProgress jobProgress = jobRepository.findByJobId(UUID.fromString(jobId));
        if (jobProgress != null) {
            jobProgress.setJobStatus(status);
            jobRepository.save(jobProgress);
        } else {
            throw new SqsProducerException("Job with ID " + jobId + " not found");
        }
    }
}
