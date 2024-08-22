package org.abx.virturalpet.controller;

import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collections;
import java.util.List;
import org.abx.virturalpet.dto.ImmutableListObjectsRequestDto;
import org.abx.virturalpet.dto.ImmutableUploadServiceDto;
import org.abx.virturalpet.dto.ListObjectsRequestDto;
import org.abx.virturalpet.dto.UploadServiceDto;
import org.abx.virturalpet.service.S3Service;
import org.abx.virturalpet.service.UploadService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WebMvcTest(UploadServiceController.class)
public class UploadServiceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UploadService uploadService;

    @MockBean
    private S3Service s3Service;

    private ListObjectsRequestDto listObjectsRequestDto;
    private UploadServiceDto uploadServiceDto;

    @BeforeEach
    void before() {
        listObjectsRequestDto = ImmutableListObjectsRequestDto.builder()
                .buckName("test-bucket")
                .prefix("test-prefix")
                .offset(0)
                .limit(10)
                .build();

        uploadServiceDto = ImmutableUploadServiceDto.builder()
                .s3Key("test-key")
                .fileName("test-file")
                .userId("test-user")
                .photoId("test-photo")
                .timestamp("2023-07-01T12:00:00")
                .metadata("test-metadata")
                .build();
    }

    @Test
    public void testUploadMediaRequestSuccess() throws Exception {
        MockMultipartFile file =
                new MockMultipartFile("file", "test.txt", MediaType.TEXT_PLAIN_VALUE, "Test content".getBytes());

        UploadServiceDto dto = ImmutableUploadServiceDto.builder()
                .fileName("test.txt")
                .userId("user1")
                .timestamp("2023-01-01T00:00:00Z")
                .metadata("some metadata")
                .build();

        Mockito.when(uploadService.uploadMediaRequest(
                        Mockito.anyString(), Mockito.anyString(), Mockito.any(byte[].class)))
                .thenReturn(dto);

        mockMvc.perform(MockMvcRequestBuilders.multipart("/media/upload")
                        .file(file)
                        .param("userId", "user1")
                        .param("timestamp", "2023-01-01T00:00:00Z")
                        .param("metadata", "some metadata")
                        .param("photoId", "photo1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.file_name").value("test.txt"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.user_id").value("user1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.timestamp").value("2023-01-01T00:00:00Z"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.metadata").value("some metadata"));
    }

    @Test
    public void testUploadMediaRequestFileEmpty() throws Exception {
        MockMultipartFile emptyFile =
                new MockMultipartFile("file", "empty.txt", MediaType.TEXT_PLAIN_VALUE, new byte[0]);

        mockMvc.perform(MockMvcRequestBuilders.multipart("/media/upload")
                        .file(emptyFile)
                        .param("userId", "user1")
                        .param("timestamp", "2023-01-01T00:00:00Z")
                        .param("metadata", "some metadata")
                        .param("photoId", "photo1"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void testUploadMediaRequestServerError() throws Exception {
        MockMultipartFile file =
                new MockMultipartFile("file", "test.txt", MediaType.TEXT_PLAIN_VALUE, "Test content".getBytes());

        Mockito.when(
                uploadService.uploadMediaRequest(Mockito.anyString(), Mockito.anyString(), Mockito.any(byte[].class)));

        mockMvc.perform(MockMvcRequestBuilders.multipart("/media/upload")
                        .file(file)
                        .param("userId", "user1")
                        .param("timestamp", "2023-01-01T00:00:00Z")
                        .param("metadata", "some metadata")
                        .param("photoId", "photo1"))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError());
    }

    @Test
    public void testListObjects() throws Exception {
        List<UploadServiceDto> uploadServiceDtos = Collections.singletonList(uploadServiceDto);

        when(s3Service.listObjects(listObjectsRequestDto.getBuckName(), listObjectsRequestDto.getPrefix()))
                .thenReturn(uploadServiceDtos);
        mockMvc.perform(MockMvcRequestBuilders.post("/media/upload/list-objects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(listObjectsRequestDto)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].s3_key").value("test-key"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].file_name").value("test-file"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].user_id").value("test-user"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].photo_id").value("test-photo"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].timestamp").value("2023-07-01T12:00:00"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].metadata").value("test-metadata"));
    }

    @Test
    public void testListObjects_NotFound() throws Exception {
        when(s3Service.listObjects(listObjectsRequestDto.getBuckName(), listObjectsRequestDto.getPrefix()))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(MockMvcRequestBuilders.post("/media/upload/list-objects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(listObjectsRequestDto)))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void testListObjectsWithPagination() throws Exception {
        List<UploadServiceDto> uploadServiceDtos = Collections.singletonList(uploadServiceDto);

        when(s3Service.listObjectsWithPagination(
                        listObjectsRequestDto.getBuckName(),
                        listObjectsRequestDto.getPrefix(),
                        listObjectsRequestDto.getOffset(),
                        listObjectsRequestDto.getLimit()))
                .thenReturn(uploadServiceDtos);

        mockMvc.perform(MockMvcRequestBuilders.post("/media/upload/list-objects-with-pagination")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(listObjectsRequestDto)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].s3_key").value("test-key"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].file_name").value("test-file"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].user_id").value("test-user"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].photo_id").value("test-photo"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].timestamp").value("2023-07-01T12:00:00"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].metadata").value("test-metadata"));
    }

    @Test
    void testListObjectsWithPagination_NotFound() throws Exception {
        when(s3Service.listObjectsWithPagination(
                        listObjectsRequestDto.getBuckName(),
                        listObjectsRequestDto.getPrefix(),
                        listObjectsRequestDto.getOffset(),
                        listObjectsRequestDto.getLimit()))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(MockMvcRequestBuilders.post("/media/upload/list-objects-with-pagination")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(listObjectsRequestDto)))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }
}
