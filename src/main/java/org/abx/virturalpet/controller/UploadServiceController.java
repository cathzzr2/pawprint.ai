package org.abx.virturalpet.controller;

import java.util.Base64;
import org.abx.virturalpet.dto.UploadServiceDto;
import org.abx.virturalpet.service.UploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class UploadServiceController {
  private final UploadService uploadService;

  @Autowired
  public UploadServiceController(UploadService uploadService) {
    this.uploadService = uploadService;
  }


  @RequestMapping(value="/media/upload", method = RequestMethod.POST)
  public ResponseEntity<UploadServiceDto> uploadMediaRequest(
      @RequestBody UploadServiceDto uploadServiceDto, @RequestParam("file") MultipartFile file) {
    String fileName = file.getOriginalFilename();
    byte[] fileData = Base64.getDecoder().decode(uploadServiceDto.getFileData());
    UploadServiceDto response = uploadService.uploadMediaRequest(fileName, fileData);
    return ResponseEntity.ok(response);

  }

//  @ExceptionHandler(Exception.class)
//  public ResponseEntity<?> handleStorageFileNotFound(Exception exc) {
//    return ResponseEntity.notFound().build();
//  }


}

