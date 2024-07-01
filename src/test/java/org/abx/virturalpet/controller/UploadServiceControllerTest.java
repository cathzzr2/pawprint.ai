package org.abx.virturalpet.controller;

import org.abx.virturalpet.dto.ImmutableUploadServiceDto;
import org.abx.virturalpet.dto.UploadServiceDto;
import org.abx.virturalpet.service.UploadService;
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

        Mockito.when(uploadService.uploadMediaRequest(Mockito.anyString(), Mockito.any(byte[].class)))
                .thenReturn(dto);

        mockMvc.perform(MockMvcRequestBuilders.multipart("/media/upload")
                        .file(file)
                        .param("userId", "user1")
                        .param("timestamp", "2023-01-01T00:00:00Z")
                        .param("metadata", "some metadata"))
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
                        .param("metadata", "some metadata"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void testUploadMediaRequestServerError() throws Exception {
        MockMultipartFile file =
                new MockMultipartFile("file", "test.txt", MediaType.TEXT_PLAIN_VALUE, "Test content".getBytes());

        Mockito.when(uploadService.uploadMediaRequest(Mockito.anyString(), Mockito.any(byte[].class)))
                .thenThrow(new RuntimeException("Runtime Exception"));

        mockMvc.perform(MockMvcRequestBuilders.multipart("/media/upload")
                        .file(file)
                        .param("userId", "user1")
                        .param("timestamp", "2023-01-01T00:00:00Z")
                        .param("metadata", "some metadata"))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError());
    }
}
