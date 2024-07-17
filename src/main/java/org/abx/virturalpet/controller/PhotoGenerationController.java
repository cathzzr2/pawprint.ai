package org.abx.virturalpet.controller;

import org.abx.virturalpet.dto.PhotoGenerationDto;
import org.abx.virturalpet.service.PhotoGenerationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PhotoGenerationController {

    @Autowired
    private PhotoGenerationService photoGenerationService;

    @RequestMapping(value = "/generate-img", method = RequestMethod.POST)
    public ResponseEntity<PhotoGenerationDto> generateImg(@RequestBody PhotoGenerationDto photoGenerationDto) {
        PhotoGenerationDto imgGen = photoGenerationService.generateImg(
                photoGenerationDto.getImageData(), photoGenerationDto.getUserId(), photoGenerationDto.getJobType());
        if (imgGen == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(PhotoGenerationDto.builder()
                .imageData(imgGen.getImageData())
                .imageId(imgGen.getImageId())
                .jobId(imgGen.getJobId())
                .userId(imgGen.getUserId())
                .jobType(imgGen.getJobType())
                .status(imgGen.getStatus())
                .build());
    }

    @RequestMapping(value = "/generate-img/check/{jobId}", method = RequestMethod.GET)
    public ResponseEntity<PhotoGenerationDto> checkJobStatus(@PathVariable String jobId) {
        PhotoGenerationDto imgGen = photoGenerationService.checkJobStatus(jobId);

        if (imgGen == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(
                PhotoGenerationDto.builder().status(imgGen.getStatus()).build());
    }

    @RequestMapping(value = "/generate-img/get/{imgId}", method = RequestMethod.GET)
    public ResponseEntity<PhotoGenerationDto> getGenImg(@PathVariable String imgId) {
        PhotoGenerationDto imgGen = photoGenerationService.getGenImg(imgId);

        if (imgGen == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(
                PhotoGenerationDto.builder().imageData(imgGen.getImageData()).build());
    }
}
