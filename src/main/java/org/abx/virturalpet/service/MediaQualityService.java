package org.abx.virturalpet.service;

import java.sql.Timestamp;
import java.util.Optional;
import java.util.UUID;
import org.abx.virturalpet.dto.ImageGenSqsDto;
import org.abx.virturalpet.dto.ImmutableImageGenSqsDto;
import org.abx.virturalpet.dto.ImmutableImprovePhotoJbDto;
import org.abx.virturalpet.dto.ImmutableImprovedPhotoResultDto;
import org.abx.virturalpet.dto.ImprovePhotoJbDto;
import org.abx.virturalpet.dto.ImprovedPhotoResultDto;
import org.abx.virturalpet.dto.JobStatus;
import org.abx.virturalpet.dto.JobType;
import org.abx.virturalpet.model.JobProgress;
import org.abx.virturalpet.model.JobResultModel;
import org.abx.virturalpet.model.PhotoJobModel;
import org.abx.virturalpet.repository.JobProgressRepository;
import org.abx.virturalpet.repository.JobResultRepository;
import org.abx.virturalpet.repository.PhotoJobRepository;
import org.abx.virturalpet.sqs.ImageGenSqsProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class MediaQualityService {

    private static final Logger logger = LoggerFactory.getLogger(MediaQualityService.class);
    private final PhotoJobRepository photoJobRepository;
    private final JobResultRepository jobResultRepository;
    private final JobProgressRepository jobProgressRepository;
    private final ImageGenSqsProducer imageGenSqsProducer;

    public MediaQualityService(
            PhotoJobRepository photoJobRepository,
            JobResultRepository jobResultRepository,
            ImageGenSqsProducer imageGenSqsProducer,
            JobProgressRepository jobProgressRepository) {
        this.photoJobRepository = photoJobRepository;
        this.jobResultRepository = jobResultRepository;
        this.imageGenSqsProducer = imageGenSqsProducer;
        this.jobProgressRepository = jobProgressRepository;
    }

    public ImprovePhotoJbDto enqueuePhoto(UUID userId, UUID photoId, JobType jobType) {
        if (photoId == null || userId == null || jobType == null) {
            logger.error("Invalid input: photoId={}, userId={}, jobType={}", photoId, userId, jobType);
            throw new IllegalArgumentException("Photo ID, User ID, and Job Type cannot be null");
        }

        UUID jobId = UUID.randomUUID();
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        // build dto for controller
        ImmutableImprovePhotoJbDto improvePhotoJbDto = ImmutableImprovePhotoJbDto.builder()
                .photoId(photoId)
                .jobId(jobId)
                .userId(userId)
                .jobType(jobType)
                .jobSubmissionTime(timestamp)
                .build();

        // save job info in PhotoJobRepo
        PhotoJobModel photoJobModel = getPhotoJobModel(improvePhotoJbDto);
        photoJobRepository.save(photoJobModel);

        // Save job progress in MongoDB
        JobProgress jobProgress = getJobProgress(improvePhotoJbDto);
        jobProgressRepository.save(jobProgress);

        // Send message to SQS
        ImageGenSqsDto imageGenSqsDto = getImageGenSqsDto(improvePhotoJbDto);
        imageGenSqsProducer.sendMessage(imageGenSqsDto);

        return improvePhotoJbDto;
    }

    public ImprovedPhotoResultDto getImprovedPhoto(UUID jobId) {

        if (jobId == null) {
            logger.error("Invalid input: jobId cannot be null");
            throw new IllegalArgumentException("jobId cannot be null");
        }
        // handle null case
        Optional<JobResultModel> optionalJobResult = Optional.ofNullable(jobResultRepository.findByJobId(jobId));
        if (optionalJobResult.isEmpty()) {
            throw new RuntimeException("Job result not found for id: " + jobId.toString());
        }
        JobResultModel jobResult = jobResultRepository.findByJobId(jobId);
        ImprovedPhotoResultDto jobResultDto = getJobResultDto(jobResult);

        return jobResultDto;
    }

    public ImprovedPhotoResultDto getJobResultDto(JobResultModel jobResultModel) {
        return ImmutableImprovedPhotoResultDto.builder()
                .resultId(jobResultModel.getResultId())
                .userId(jobResultModel.getUserId())
                .jobId(jobResultModel.getJobId())
                .s3Key(jobResultModel.getS3Key())
                .generatedTime(jobResultModel.getGeneratedTime())
                .build();
    }

    public PhotoJobModel getPhotoJobModel(ImprovePhotoJbDto improvePhotoJbDto) {
        return new PhotoJobModel(
                improvePhotoJbDto.getJobId(),
                improvePhotoJbDto.getPhotoId(),
                improvePhotoJbDto.getUserId(),
                improvePhotoJbDto.getJobType().name(),
                improvePhotoJbDto.getJobSubmissionTime());
    }

    public ImageGenSqsDto getImageGenSqsDto(ImprovePhotoJbDto improvePhotoJbDto) {
        return ImmutableImageGenSqsDto.builder()
                .jobId(improvePhotoJbDto.getJobId().toString())
                .photoId(improvePhotoJbDto.getPhotoId().toString())
                .jobType(improvePhotoJbDto.getJobType())
                .build();
    }

    public JobProgress getJobProgress(ImprovePhotoJbDto improvePhotoJbDto) {
        return JobProgress.Builder.newBuilder()
                .withJobId(improvePhotoJbDto.getJobId())
                .withJobType(improvePhotoJbDto.getJobType().name())
                .withJobStatus(JobStatus.IN_QUEUE)
                .build();
    }
}
