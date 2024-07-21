package org.abx.virturalpet.controller;

import org.abx.virturalpet.dto.ImmutablePhotoGenerationDto;
import org.abx.virturalpet.dto.JobStatus;
import org.abx.virturalpet.dto.PhotoGenerationDto;
import org.abx.virturalpet.service.PhotoGenerationService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WebMvcTest(PhotoGenerationController.class)
public class PhotoGenerationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PhotoGenerationService photoGenerationService;

    @Test
    public void testPhotoGeneration() throws Exception {
        PhotoGenerationDto response = PhotoGenerationDto.builder()
                .imageData("base64_encoded_photo")
                .imageId("1")
                .jobId("1")
                .userId("user1")
                .jobType("enhance")
                .status(JobStatus.IN_QUEUE)
                .build();

        Mockito.when(photoGenerationService.generateImg("base64_encoded_photo", "user1", "enhance"))
                .thenReturn(response);

        String requestJsonPayload = "{\n" + "  \"image_data\": \"base64_encoded_photo\",\n"
                + "  \"user_id\": \"user1\",\n"
                + "  \"job_type\": \"enhance\"\n"
                + "}";

        mockMvc.perform(MockMvcRequestBuilders.post("/generate-img")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJsonPayload))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.image_data").value("base64_encoded_photo"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.job_id").value("1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.user_id").value("user1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.job_type").value("enhance"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.jb_status").value(JobStatus.IN_QUEUE.name()));
    }

    @Test
    public void testCheckJobStatus() throws Exception {
        PhotoGenerationDto response = ImmutablePhotoGenerationDto.builder()
                .status(JobStatus.COMPLETED)
                .jobId("5")
                .build();

        Mockito.when(photoGenerationService.checkJobStatus("5")).thenReturn(response);

        mockMvc.perform(MockMvcRequestBuilders.get("/generate-img/check/5").contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.jb_status").value("COMPLETED"));
    }

    @Test
    public void testCheckJobStatus_notFound() throws Exception {

        Mockito.when(photoGenerationService.getGenImg("100")).thenReturn(null);

        mockMvc.perform(MockMvcRequestBuilders.get("/generate-img/check/100").contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void testGetGenImg() throws Exception {
        PhotoGenerationDto response =
                PhotoGenerationDto.builder().imageData("base64_encoded_photo").build();

        Mockito.when(photoGenerationService.getGenImg("10")).thenReturn(response);

        mockMvc.perform(MockMvcRequestBuilders.get("/generate-img/get/10").contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.image_data").value("base64_encoded_photo"));
    }

    @Test
    public void testGetGenImg_notFound() throws Exception {

        Mockito.when(photoGenerationService.getGenImg("100")).thenReturn(null);

        mockMvc.perform(MockMvcRequestBuilders.get("/generate-img/get/100").contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }
}
