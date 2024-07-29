package org.abx.virturalpet.controller;

import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.Timestamp;
import java.util.UUID;
import org.abx.virturalpet.dto.ImprovePhotoJbDto;
import org.abx.virturalpet.dto.ImprovedPhotoResultDto;
import org.abx.virturalpet.dto.JobType;
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
        JobType jobType = JobType.ENHANCE;
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

    @Test
    void testImprovePhotoJbID_NotFound() throws Exception {
        UUID userId = UUID.randomUUID();
        UUID photoId = UUID.randomUUID();
        JobType jobType = JobType.ENHANCE;

        when(mediaQualityService.enqueuePhoto(userId, photoId, jobType)).thenReturn(null);

        String requestJsonPayload = String.format(
                "{\"userId\":\"%s\",\"photoId\":\"%s\",\"jobType\":\"%s\"}",
                userId.toString(), photoId.toString(), jobType);

        mockMvc.perform(MockMvcRequestBuilders.post("/improve")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJsonPayload))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void testGetImprovedPhoto_success() throws Exception {
        UUID jobId = UUID.randomUUID();
        UUID resultId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        String s3Key = "some-s3-key";
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        ImprovedPhotoResultDto mockedResponse = ImprovedPhotoResultDto.builder()
                .resultId(resultId)
                .userId(userId)
                .jobId(jobId)
                .s3Key(s3Key)
                .generatedTime(timestamp)
                .build();

        when(mediaQualityService.getImprovedPhoto(jobId)).thenReturn(mockedResponse);

        ImprovedPhotoResultDto requestDto = ImprovedPhotoResultDto.builder()
                .resultId(resultId)
                .userId(userId)
                .jobId(jobId)
                .s3Key(s3Key)
                .generatedTime(timestamp)
                .build();

        String requestJsonPayload = objectMapper.writeValueAsString(requestDto);

        mockMvc.perform(MockMvcRequestBuilders.get("/results/{jobId}", jobId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJsonPayload))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.result_id").value(resultId.toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.job_id").value(jobId.toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.s3_key").value(s3Key.toString()));
    }

    @Test
    public void testGetImprovedPhoto_notFound() throws Exception {
        UUID jobId = UUID.randomUUID();

        when(mediaQualityService.getImprovedPhoto(jobId)).thenReturn(null);

        mockMvc.perform(MockMvcRequestBuilders.get("/results/{jobId}", jobId).accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }
}
