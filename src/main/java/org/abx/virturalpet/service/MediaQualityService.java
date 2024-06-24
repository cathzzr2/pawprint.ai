package org.abx.virturalpet.service;

import java.util.UUID;
import org.abx.virturalpet.dto.ImmutableImprovePhotoJbDto;
import org.abx.virturalpet.dto.ImmutableImprovedPhotoResultDto;
import org.abx.virturalpet.dto.ImprovePhotoJbDto;
import org.abx.virturalpet.dto.ImprovedPhotoResultDto;
import org.springframework.stereotype.Service;

@Service
public class MediaQualityService {

    public ImprovePhotoJbDto enqueuePhoto(String photoFile) {
        if (photoFile == null || photoFile.isEmpty()) {
            return null;
        }

        String jobId = UUID.randomUUID().toString(); // generate new photo ID

        return ImmutableImprovePhotoJbDto.builder()
                .improvePhotoJbId(jobId)
                .photoFile(photoFile)
                .build();
    }

    public ImprovedPhotoResultDto getImprovedPhoto(String improvedPhotoId) {
        if (improvedPhotoId == null || improvedPhotoId.isEmpty()) {
            return null;
        }

        String improvedPhotoUrl = "http://example.com/path/to/photo/" + improvedPhotoId + ".jpg";
        return ImmutableImprovedPhotoResultDto.builder()
                .improvedPhotoUrl(improvedPhotoUrl)
                .build();
    }
}
