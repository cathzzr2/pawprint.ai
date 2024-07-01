package org.abx.virturalpet.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Base64;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import org.abx.virturalpet.dto.ImmutableUploadServiceDto;
import org.abx.virturalpet.dto.UploadServiceDto;
import org.abx.virturalpet.service.UploadService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
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
  public void testUploadMediaRequest_returnOk() throws Exception {
    UploadServiceDto uploadServiceDto = ImmutableUploadServiceDto.builder()
        .fileName("testFile.txt")
        .fileData(new byte[] { 1, 2, 3, 4, 5 })
        .userId("userId")
        .fileType("fileType")
        .timestamp("timestamp")
        .metadata("metadata")
        .statusMsg("media upload success")
        .s3Key("s3Key") // This is set in the response
        .build();

    when(uploadService.uploadMediaRequest(anyString(), any(byte[].class)))
        .thenReturn(uploadServiceDto);

    MockMultipartFile file = new MockMultipartFile("file", "testFile.txt", "text/plain", new byte[] { 1, 2, 3, 4, 5 });

    String requestJsonPayload = "{\n"
        + "\"file_name\": \"testFile.txt\",\n"
        + "\"file_data\": \"" + Base64.getEncoder().encodeToString(new byte[] { 1, 2, 3, 4, 5 }) + "\",\n"
        + "\"user_id\": \"userId\",\n"
        + "\"file_type\": \"fileType\",\n"
        + "\"timestamp\": \"timestamp\",\n"
        + "\"metadata\": \"metadata\"\n"
        + "}";

    mockMvc.perform(multipart("/media/upload")
            .file(file)
            .content(requestJsonPayload)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status_msg").value("media upload success"))
        .andExpect(jsonPath("$.file_name").value("testFile.txt"))
        .andExpect(jsonPath("$.file_data").value(Base64.getEncoder().encodeToString(new byte[] { 1, 2, 3, 4, 5 })))
        .andExpect(jsonPath("$.user_id").value("userId"))
        .andExpect(jsonPath("$.file_type").value("fileType"))
        .andExpect(jsonPath("$.timestamp").value("timestamp"))
        .andExpect(jsonPath("$.s3_key").value("s3Key")) // Expect the s3_key in the response
        .andExpect(jsonPath("$.metadata").value("metadata"));
  }


  @Test
  public void testUploadMediaRequest_mediaNotProvided() throws Exception {
    UploadServiceDto mockResponse = ImmutableUploadServiceDto.builder()
        .statusMsg("media not provided")
        .fileName("")
        .fileData(new byte[0])
        .userId("")
        .fileType("")
        .timestamp("")
        .s3Key("")
        .metadata("")
        .build();

    when(uploadService.uploadMediaRequest("", new byte[0])).thenReturn(mockResponse);

    String requestJsonPayload = "{\n"
        + "\"file_name\": \"\",\n"
        + "\"file_data\": \"\",\n"
        + "\"user_id\": \"\",\n"
        + "\"file_type\": \"\",\n"
        + "\"timestamp\": \"\",\n"
        + "\"metadata\": \"\"\n"
        + "}";

    mockMvc.perform(post("/media/upload")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestJsonPayload))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status_msg").value("media not provided"))
        .andExpect(jsonPath("$.file_name").value(""))
        .andExpect(jsonPath("$.file_data").value(""))
        .andExpect(jsonPath("$.user_id").value(""))
        .andExpect(jsonPath("$.file_type").value(""))
        .andExpect(jsonPath("$.timestamp").value(""))
        .andExpect(jsonPath("$.s3_key").value(""))
        .andExpect(jsonPath("$.metadata").value(""));
  }

  @Test
  public void testUploadMediaRequest_returnFailed() throws Exception {
    // Mock the response from the upload service
    when(uploadService.uploadMediaRequest(anyString(), any(byte[].class)))
        .thenReturn(ImmutableUploadServiceDto.builder()
            .fileName("testFile.txt")
            .fileData(new byte[] { 1, 2, 3, 4, 5 })
            .userId("userId")
            .statusMsg("media upload failed")
            .fileType("fileType")
            .timestamp("timestamp")
            .s3Key("")
            .metadata("metadata")
            .build());

    // Create a MockMultipartFile for the file data
    MockMultipartFile mockMultipartFile = new MockMultipartFile("file", "testFile.txt", "text/plain", "test data".getBytes());

    // Build the multipart request
    MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.multipart("/media/upload")
        .file(mockMultipartFile)
        .param("file_name", "testFile.txt")
        .param("user_id", "userId")
        .param("file_type", "fileType")
        .param("timestamp", "timestamp")
        .param("metadata", "metadata")
        .contentType(MediaType.MULTIPART_FORM_DATA);

    // Perform the request and set the expected responses
    mockMvc.perform(builder)
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status_msg").value("media upload failed"))
        .andExpect(jsonPath("$.file_name").value("testFile.txt"))
        .andExpect(jsonPath("$.file_data").value("AQIDBAU=")) // Base64 encoded value of byte[] { 1, 2, 3, 4, 5 }
        .andExpect(jsonPath("$.user_id").value("userId"))
        .andExpect(jsonPath("$.file_type").value("fileType"))
        .andExpect(jsonPath("$.timestamp").value("timestamp"))
        .andExpect(jsonPath("$.s3_key").value(""))
        .andExpect(jsonPath("$.metadata").value("metadata"));
  }


}
