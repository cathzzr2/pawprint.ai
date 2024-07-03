package org.abx.virturalpet.service;

import java.sql.Timestamp;
import java.util.UUID;
import org.abx.virturalpet.dto.ImmutableImprovePhotoJbDto;
import org.abx.virturalpet.dto.ImmutableImprovedPhotoResultDto;
import org.abx.virturalpet.dto.ImprovePhotoJbDto;
import org.abx.virturalpet.dto.ImprovedPhotoResultDto;
import org.springframework.stereotype.Service;

@Service
public class MediaQualityService {

    public ImprovePhotoJbDto enqueuePhoto(UUID userId, UUID photoId, String jobType) {
        if (photoId == null || userId == null || jobType == null) {
            return null;
        } // check how to check uuid valid

        UUID jobId = UUID.randomUUID();
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        return ImmutableImprovePhotoJbDto.builder()
                .photoId(photoId)
                .jobId(jobId)
                .userId(userId)
                .jobType(jobType)
                .jobSubmissionTime(timestamp)
                .build();
    }

    public ImprovedPhotoResultDto getImprovedPhoto(UUID userId, UUID jobId, String s3Key) {
        if (jobId == null || userId == null || s3Key == null) {
            return null;
        }

        UUID resultId = UUID.randomUUID();
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        return ImmutableImprovedPhotoResultDto.builder()
                .resultId(resultId)
                .userId(userId)
                .jobId(jobId)
                .s3Key(s3Key)
                .generatedTime(timestamp)
                .build();
    }
}
