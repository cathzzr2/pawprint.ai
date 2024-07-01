package org.abx.virturalpet.controller;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import org.abx.virturalpet.dto.ImmutableUploadServiceDto;
import org.abx.virturalpet.dto.UploadServiceDto;
import org.abx.virturalpet.dto.UploadServiceRequest;
import org.abx.virturalpet.service.UploadService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class UploadServiceController {
    private final UploadService uploadService;

    public UploadServiceController(UploadService uploadService) {
        this.uploadService = uploadService;
    }

    @RequestMapping(value = "/media/upload", method = RequestMethod.POST)
    public ResponseEntity<UploadServiceDto> uploadMediaRequest(@ModelAttribute UploadServiceRequest request)
            throws IOException {

        MultipartFile file = request.getFile();
        if (file.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Path tempFile = Files.createTempFile("upload_", "_" + file.getOriginalFilename());

        try (InputStream inputStream = file.getInputStream();
                FileOutputStream outputStream = new FileOutputStream(tempFile.toFile())) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            String fileName = file.getOriginalFilename();
            byte[] fileData = file.getBytes();

            UploadServiceDto uploadServiceDto = ImmutableUploadServiceDto.builder()
                    .fileName(fileName)
                    .userId(request.getUserId())
                    .timestamp(request.getTimestamp())
                    .metadata(request.getMetadata())
                    .build();

            UploadServiceDto response = uploadService.uploadMediaRequest(fileName, fileData);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // TODO: LOG ERROR
            System.out.println(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
