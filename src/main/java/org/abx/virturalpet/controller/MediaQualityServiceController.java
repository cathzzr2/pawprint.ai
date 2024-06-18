package org.abx.virturalpet.controller;

import org.abx.virturalpet.dto.ImprovePhotoDto;
import org.abx.virturalpet.service.MediaQualityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MediaQualityServiceController {
    @Autowired
    private MediaQualityService mediaQualityService;

    @RequestMapping(value = "/improve", method = RequestMethod.POST)
    public ResponseEntity<ImprovePhotoDto> improvePhoto(@RequestBody ImprovePhotoDto improvePhotoDto) {
        String photoFile = improvePhotoDto.getPhotoFile();
        ImprovePhotoDto res = mediaQualityService.improvePhoto(photoFile);
        return ResponseEntity.ok(res);
    }

    @RequestMapping(value = "/results/{improvedPhotoId}", method = RequestMethod.GET)
    public ResponseEntity<ImprovePhotoDto> getImprovedPhoto(@PathVariable String improvedPhotoId) {
        ImprovePhotoDto res = mediaQualityService.getImprovedPhoto(improvedPhotoId);
        return ResponseEntity.ok(res);
    }
}
