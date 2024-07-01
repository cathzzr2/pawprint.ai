package org.abx.virturalpet.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.abx.virturalpet.dto.ImmutableUploadServiceDto;
import org.abx.virturalpet.dto.UploadServiceDto;
import org.abx.virturalpet.service.UploadService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(UploadServiceController.class)
public class UploadServiceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UploadService uploadService;

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

        when(uploadService.uploadMediaRequest(anyString(), any(byte[].class))).thenReturn(dto);

        mockMvc.perform(multipart("/media/upload")
                        .file(file)
                        .param("userId", "user1")
                        .param("timestamp", "2023-01-01T00:00:00Z")
                        .param("metadata", "some metadata"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.file_name").value("test.txt"))
                .andExpect(jsonPath("$.user_id").value("user1"))
                .andExpect(jsonPath("$.timestamp").value("2023-01-01T00:00:00Z"))
                .andExpect(jsonPath("$.metadata").value("some metadata"));
    }

    @Test
    public void testUploadMediaRequestFileEmpty() throws Exception {
        MockMultipartFile emptyFile =
                new MockMultipartFile("file", "empty.txt", MediaType.TEXT_PLAIN_VALUE, new byte[0]);

        mockMvc.perform(multipart("/media/upload")
                        .file(emptyFile)
                        .param("userId", "user1")
                        .param("timestamp", "2023-01-01T00:00:00Z")
                        .param("metadata", "some metadata"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testUploadMediaRequestServerError() throws Exception {
        MockMultipartFile file =
                new MockMultipartFile("file", "test.txt", MediaType.TEXT_PLAIN_VALUE, "Test content".getBytes());

        when(uploadService.uploadMediaRequest(anyString(), any(byte[].class)))
                .thenThrow(new RuntimeException("Runtime Exception"));

        mockMvc.perform(multipart("/media/upload")
                        .file(file)
                        .param("userId", "user1")
                        .param("timestamp", "2023-01-01T00:00:00Z")
                        .param("metadata", "some metadata"))
                .andExpect(status().isInternalServerError());
    }
}
