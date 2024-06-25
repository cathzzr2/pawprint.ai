package org.abx.virturalpet.controller;

import org.abx.virturalpet.dto.PhotoGenerationDto;
import org.abx.virturalpet.service.PhotoGenerationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class PhotoGenerationController {

    @Autowired
    private PhotoGenerationService photoGenerationService;

    @RequestMapping(value = "/generate-img", method = RequestMethod.POST)
    public ResponseEntity<PhotoGenerationDto> generateImg(@RequestBody PhotoGenerationDto photoGenerationDto) {
        PhotoGenerationDto imgGen = photoGenerationService.generateImg(photoGenerationDto.getImageData());
        if ( imgGen == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(PhotoGenerationDto.builder()
                .imageData(imgGen.getImageData())
                .jobId(imgGen.getJobId())
                .build());
    }

    @RequestMapping(value = "/generate-img/check/{jobId}", method = RequestMethod.GET)
    public ResponseEntity<PhotoGenerationDto> checkJobStatus(@PathVariable String jobId) {
        PhotoGenerationDto imgGen = photoGenerationService.checkJobStatus(jobId);

        if (imgGen == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(PhotoGenerationDto.builder()
                .completed(imgGen.getCompleted())
                .build());
    }

    @RequestMapping(value = "/generate-img/get/{imgId}", method = RequestMethod.GET)
    public ResponseEntity<PhotoGenerationDto> getGenImg(@PathVariable String imgId) {
        PhotoGenerationDto imgGen = photoGenerationService.getGenImg(imgId);

        if (imgGen == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(PhotoGenerationDto.builder()
                .imageData(imgGen.getImageData())
                .build());
    }
}
