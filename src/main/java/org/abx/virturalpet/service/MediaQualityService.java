package org.abx.virturalpet.service;

import java.util.UUID;
import org.abx.virturalpet.dto.ImmutableImprovePhotoDto;
import org.abx.virturalpet.dto.ImprovePhotoDto;
import org.springframework.stereotype.Service;

@Service
public class MediaQualityService {
    private static final String MEDIA_NOT_FOUND = "media not found";
    private static final String MEDIA_NOT_PROVIDED = "media not provided";
    private static final String IMPROVE_SUCCESS = "Media improved successfully";

    private static final String GET_URL_SUCCESS = "Successfully retrieved media URL";

    public ImprovePhotoDto improvePhoto(String photoFile) {
        if (photoFile == null || photoFile.isEmpty()) {
            return ImmutableImprovePhotoDto.builder()
                    .statusCode(1)
                    .statusMsg(MEDIA_NOT_PROVIDED)
                    .improvedPhotoId(null)
                    .build();
        }

        String improvedPhotoId = UUID.randomUUID().toString(); // generate new photo ID

        return ImmutableImprovePhotoDto.builder()
                .statusCode(0)
                .statusMsg(IMPROVE_SUCCESS)
                .improvedPhotoId(improvedPhotoId)
                .build();
    }

    public ImprovePhotoDto getImprovedPhoto(String improvedPhotoId) {
        if (improvedPhotoId == null || improvedPhotoId.isEmpty()) {
            return ImmutableImprovePhotoDto.builder()
                    .statusCode(1)
                    .statusMsg(MEDIA_NOT_FOUND)
                    .build();
        }

        String improvedPhotoUrl = "http://example.com/path/to/photo/" + improvedPhotoId + ".jpg";
        return ImmutableImprovePhotoDto.builder()
                .statusCode(0)
                .statusMsg(GET_URL_SUCCESS)
                .improvedPhotoUrl(improvedPhotoUrl)
                .build();
    }
}
