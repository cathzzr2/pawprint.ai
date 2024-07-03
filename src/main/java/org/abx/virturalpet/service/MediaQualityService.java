package org.abx.virturalpet.service;

import java.sql.Timestamp;
import java.util.UUID;
import org.abx.virturalpet.dto.ImmutableImprovePhotoJbDto;
import org.abx.virturalpet.dto.ImmutableImprovedPhotoResultDto;
import org.abx.virturalpet.dto.ImprovePhotoJbDto;
import org.abx.virturalpet.dto.ImprovedPhotoResultDto;
import org.abx.virturalpet.model.JobResultModel;
import org.abx.virturalpet.model.PhotoJobModel;
import org.abx.virturalpet.repository.JobResultRepository;
import org.abx.virturalpet.repository.PhotoJobRepository;
import org.springframework.stereotype.Service;

@Service
public class MediaQualityService {

    private final PhotoJobRepository photoJobRepository;
    private final JobResultRepository jobResultRepository;

    public MediaQualityService(PhotoJobRepository photoJobRepository, JobResultRepository jobResultRepository) {
        this.photoJobRepository = photoJobRepository;
        this.jobResultRepository = jobResultRepository;
    }

    public ImprovePhotoJbDto enqueuePhoto(UUID userId, UUID photoId, String jobType) {
        if (photoId == null || userId == null || jobType == null) {
            return null;
        }

        UUID jobId = UUID.randomUUID();
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        ImmutableImprovePhotoJbDto newPhotoJob = ImmutableImprovePhotoJbDto.builder()
                .photoId(photoId)
                .jobId(jobId)
                .userId(userId)
                .jobType(jobType)
                .jobSubmissionTime(timestamp)
                .build();

        PhotoJobModel jobModel = fromJobDto(newPhotoJob);
        photoJobRepository.save(jobModel);

        return ImmutableImprovePhotoJbDto.builder()
                .photoId(photoId)
                .jobId(jobId)
                .userId(userId)
                .jobType(jobType)
                .jobSubmissionTime(timestamp)
                .build();
    }

    public ImprovedPhotoResultDto getImprovedPhoto(UUID jobId) {
        if (jobId == null) {
            return null;
        }

        JobResultModel jobResult = jobResultRepository.findByJobId(jobId);
        ImprovedPhotoResultDto jobResultDto = fromResultModel(jobResult);

        UUID resultId = UUID.randomUUID();
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        return jobResultDto;
    }

    public ImprovedPhotoResultDto fromResultModel(JobResultModel jobResultModel) {
        return ImmutableImprovedPhotoResultDto.builder()
                .resultId(jobResultModel.getResultId())
                .userId(jobResultModel.getUserId())
                .jobId(jobResultModel.getJobId())
                .s3Key(jobResultModel.getS3Key())
                .generatedTime(jobResultModel.getGeneratedTime())
                .build();
    }

    public PhotoJobModel fromJobDto(ImprovePhotoJbDto improvePhotoJbDto) {
        return new PhotoJobModel(
                improvePhotoJbDto.getJobId(),
                improvePhotoJbDto.getPhotoId(),
                improvePhotoJbDto.getUserId(),
                improvePhotoJbDto.getJobType(),
                improvePhotoJbDto.getJobSubmissionTime());
    }
}
