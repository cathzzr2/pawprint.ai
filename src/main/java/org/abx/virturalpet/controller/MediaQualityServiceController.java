package org.abx.virturalpet.controller;

import java.util.UUID;
import org.abx.virturalpet.dto.ImprovePhotoJbDto;
import org.abx.virturalpet.dto.ImprovedPhotoResultDto;
import org.abx.virturalpet.service.MediaQualityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MediaQualityServiceController {
    private static final Logger logger = LoggerFactory.getLogger(MediaQualityServiceController.class);

    @Autowired
    private MediaQualityService mediaQualityService;

    @RequestMapping(value = "/improve", method = RequestMethod.POST)
    public ResponseEntity<ImprovePhotoJbDto> improvePhotoJbID(@RequestBody ImprovePhotoJbDto improvePhotoJbDto) {
        UUID userId = improvePhotoJbDto.getUserId();
        UUID photoId = improvePhotoJbDto.getPhotoId();
        String photoType = improvePhotoJbDto.getJobType();
        ImprovePhotoJbDto res = mediaQualityService.enqueuePhoto(userId, photoId, photoType);
        if (res == null) {
            logger.warn("No improvement found for photo with ID: {}", improvePhotoJbDto.getJobId());
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(ImprovePhotoJbDto.builder()
                .photoId(res.getPhotoId())
                .userId(res.getUserId())
                .jobId(res.getJobId())
                .jobType(res.getJobType())
                .jobSubmissionTime(res.getJobSubmissionTime())
                .build());
    }

    @RequestMapping(value = "/results/{jobId}", method = RequestMethod.GET)
    public ResponseEntity<ImprovedPhotoResultDto> getImprovedPhoto(
            @PathVariable ImprovedPhotoResultDto improvedPhotoResultDto) {
        UUID userId = improvedPhotoResultDto.getUserId();
        UUID jobId = improvedPhotoResultDto.getJobId();
        String s3Key = improvedPhotoResultDto.getS3Key();
        ImprovedPhotoResultDto res = mediaQualityService.getImprovedPhoto(userId, jobId, s3Key);

        if (res == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(ImprovedPhotoResultDto.builder()
                .resultId(res.getResultId())
                .userId(res.getUserId())
                .jobId(res.getJobId())
                .s3Key(res.getS3Key())
                .generatedTime(res.getGeneratedTime())
                .build());
    }
}
