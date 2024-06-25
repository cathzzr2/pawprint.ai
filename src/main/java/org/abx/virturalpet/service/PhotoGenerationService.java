package org.abx.virturalpet.service;

import java.util.UUID;
import org.abx.virturalpet.dto.PhotoGenerationDto;
import org.abx.virturalpet.dto.ImmutablePhotoGenerationDto;
import org.springframework.stereotype.Service;

@Service
public class PhotoGenerationService {
    public PhotoGenerationDto generateImg(String imageData) {
        if (imageData == null || imageData.isEmpty()) {
            return null;
        }

        String imageId = UUID.randomUUID().toString();
        String jobId = UUID.randomUUID().toString();

        return ImmutablePhotoGenerationDto.builder()
                .imageData(imageData)
                .imageId(imageId)
                .jobId(jobId)
                .build();
    }

    public PhotoGenerationDto checkJobStatus(String jobId) {
        if (jobId == null || jobId.isEmpty()) {
            return null;
        }

        boolean ifComplete = true;

        return ImmutablePhotoGenerationDto.builder()
                .completed(ifComplete)
                .build();
    }

    public  PhotoGenerationDto getGenImg(String imageId) {
        if (imageId == null || imageId.isEmpty()) {
            return null;
        }

        String genPhoto = "base64_encoded_photo";
        return ImmutablePhotoGenerationDto.builder()
                .imageData(genPhoto)
                .build();
    }
}
