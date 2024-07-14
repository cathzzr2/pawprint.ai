package org.abx.virturalpet.controller;

import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.Timestamp;
import java.util.UUID;
import org.abx.virturalpet.dto.ImprovePhotoJbDto;
import org.abx.virturalpet.service.MediaQualityService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@ExtendWith(SpringExtension.class)
@WebMvcTest(MediaQualityServiceController.class)
public class MediaQualityServiceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MediaQualityService mediaQualityService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testImprovePhotoJbID_success() throws Exception {
        UUID userId = UUID.randomUUID();
        UUID photoId = UUID.randomUUID();
        String jobType = "enhance";
        UUID jobId = UUID.randomUUID();
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        ImprovePhotoJbDto mockedResponse = ImprovePhotoJbDto.builder()
                .userId(userId)
                .photoId(photoId)
                .jobType(jobType)
                .jobId(jobId)
                .jobSubmissionTime(timestamp)
                .build();

        when(mediaQualityService.enqueuePhoto(userId, photoId, jobType)).thenReturn(mockedResponse);

        ImprovePhotoJbDto requestDto = ImprovePhotoJbDto.builder()
                .userId(userId)
                .photoId(photoId)
                .jobType(jobType)
                .jobId(jobId)
                .jobSubmissionTime(timestamp)
                .build();

        String requestJsonPayload = objectMapper.writeValueAsString(requestDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/improve")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJsonPayload))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.job_id").value(jobId.toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.photo_id").value(photoId.toString()));
    }
}
