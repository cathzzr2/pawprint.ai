package org.abx.virturalpet.service;

import java.util.UUID;
import org.abx.virturalpet.dto.ImmutableImprovePhotoDto;
import org.abx.virturalpet.dto.ImprovePhotoDto;
import org.springframework.stereotype.Service;

@Service
public class MediaQualityService {
    private static final String MEDIA_NOT_FOUND = "media not found";
    private static final String MEDIA_NOT_PROVIDED = "media not provided";
    private static final String SUCCESS = "media improved";

    public ImprovePhotoDto improvePhoto(String photoFile) {
        if (photoFile == null || photoFile.isEmpty()) {
            return ImmutableImprovePhotoDto.builder()
                    .statusCode(1)
                    .statusMsg(MEDIA_NOT_PROVIDED)
                    .photoFile("")
                    .improvedPhotoId(null)
                    .build();
        }

        String improvedPhotoId = UUID.randomUUID().toString(); // generate new photo ID

        return ImmutableImprovePhotoDto.builder()
                .statusCode(0)
                .statusMsg(SUCCESS)
                .photoFile(photoFile)
                .improvedPhotoId(improvedPhotoId)
                .build();
    }

    public ImprovePhotoDto getImprovedPhoto(String improvedPhotoId) {
        if (improvedPhotoId == null || improvedPhotoId.isEmpty()) {
            return ImmutableImprovePhotoDto.builder()
                    .statusCode(1)
                    .statusMsg(MEDIA_NOT_FOUND)
                    .photoFile("")
                    .improvedPhotoId(null)
                    .build();
        }

        String improvedPhotoUrl = "http://example.com/path/to/photo/" + improvedPhotoId + ".jpg";
        return ImmutableImprovePhotoDto.builder()
                .statusCode(0)
                .statusMsg(SUCCESS)
                .photoFile("")
                .improvedPhotoId(improvedPhotoId)
                .improvedPhotoUrl(improvedPhotoUrl)
                .build();
    }
}
