package org.abx.virturalpet.controller;

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
        String photoFile = improvePhotoJbDto.getPhotoFile();
        ImprovePhotoJbDto res = mediaQualityService.enqueuePhoto(photoFile);
        if (res == null) {
            logger.warn("No improvement found for photo with ID: {}", improvePhotoJbDto.getImprovePhotoJbId());
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(ImprovePhotoJbDto.builder()
                .improvePhotoJbId(res.getImprovePhotoJbId())
                .photoFile(res.getPhotoFile())
                .build());
    }

    @RequestMapping(value = "/results/{improvedPhotoId}", method = RequestMethod.GET)
    public ResponseEntity<ImprovedPhotoResultDto> getImprovedPhoto(@PathVariable String improvedPhotoId) {
        ImprovedPhotoResultDto res = mediaQualityService.getImprovedPhoto(improvedPhotoId);

        if (res == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(ImprovedPhotoResultDto.builder()
                .improvedPhotoUrl(res.getImprovedPhotoUrl())
                .build());
    }
}
