package org.abx.virturalpet.controller;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import org.abx.virturalpet.dto.ImmutableUploadServiceDto;
import org.abx.virturalpet.dto.ListObjectsRequestDto;
import org.abx.virturalpet.dto.UploadServiceDto;
import org.abx.virturalpet.dto.UploadServiceRequest;
import org.abx.virturalpet.service.S3Service;
import org.abx.virturalpet.service.UploadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class UploadServiceController {
    private static final Logger logger = LoggerFactory.getLogger(UploadServiceController.class);
    private final UploadService uploadService;
    private final S3Service s3Service;

    public UploadServiceController(UploadService uploadService, S3Service s3Service) {
        this.uploadService = uploadService;
        this.s3Service = s3Service;
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

            UploadServiceDto response = uploadService.uploadMediaRequest(request.getPhotoId(), fileName, fileData);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error processing file upload", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * controller for uploadService: list objects with Pagination
     */
    @PostMapping("/media/upload/list-objects")
    public ResponseEntity<List<UploadServiceDto>> listObjects(
            @RequestBody ListObjectsRequestDto listObjectsRequestDto) {
        List<UploadServiceDto> results =
                s3Service.listObjects(listObjectsRequestDto.getBuckName(), listObjectsRequestDto.getPrefix());
        if (results.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        List<UploadServiceDto> immutableResults = results.stream()
                .map(res -> ImmutableUploadServiceDto.builder()
                        .s3Key(res.getS3Key())
                        .fileName(res.getFileName())
                        .userId(res.getUserId())
                        .photoId(res.getPhotoId())
                        .timestamp(res.getTimestamp())
                        .metadata(res.getMetadata())
                        .build())
                .collect(Collectors.toList());

        return ResponseEntity.ok(immutableResults);
    }

    @PostMapping("/media/upload/list-objects-with-pagination")
    public ResponseEntity<List<UploadServiceDto>> listObjectsWithPagination(
            @RequestBody ListObjectsRequestDto listObjectsRequestDto) {
        List<UploadServiceDto> results = s3Service.listObjectsWithPagination(
                listObjectsRequestDto.getBuckName(),
                listObjectsRequestDto.getPrefix(),
                listObjectsRequestDto.getOffset(),
                listObjectsRequestDto.getLimit());

        if (results.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        List<UploadServiceDto> immutableResults = results.stream()
                .map(res -> ImmutableUploadServiceDto.builder()
                        .s3Key(res.getS3Key())
                        .fileName(res.getFileName())
                        .userId(res.getUserId())
                        .photoId(res.getPhotoId())
                        .timestamp(res.getTimestamp())
                        .metadata(res.getMetadata())
                        .build())
                .collect(Collectors.toList());

        return ResponseEntity.ok(immutableResults);
    }
}
