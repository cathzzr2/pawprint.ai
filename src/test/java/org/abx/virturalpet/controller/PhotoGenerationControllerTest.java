package org.abx.virturalpet.controller;

import static org.mockito.Mockito.when;

import org.abx.virturalpet.dto.PhotoGenerationDto;
import org.abx.virturalpet.service.PhotoGenerationService;
import org.junit.jupiter.api.Test;
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
                .jobId("1")
                .build();

        when(photoGenerationService.generateImg("base64_encoded_photo")).thenReturn(response);

        String requestJsonPayload = "{\n"
                + "\"image_data\": \"base64_encoded_photo\",\n"
                + "\"job_id\": \"1\"\n"
                + "}";

        mockMvc.perform(MockMvcRequestBuilders.post("/generate-img")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJsonPayload))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.image_data").value("base64_encoded_photo"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.job_id").value("1"));
    }

    @Test
    public void testCheckJobStatus() throws Exception {
        PhotoGenerationDto response = PhotoGenerationDto.builder()
                .completed(true)
                .build();

        when(photoGenerationService.checkJobStatus("5")).thenReturn(response);

        mockMvc.perform(MockMvcRequestBuilders.get("/generate-img/check/5")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.completed").value(true));
    }

    @Test
    public void testCheckJobStatus_notFound() throws Exception {

        when(photoGenerationService.getGenImg("100")).thenReturn(null);

        mockMvc.perform(MockMvcRequestBuilders.get("/generate-img/check/100")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void testGetGenImg() throws Exception {
        PhotoGenerationDto response = PhotoGenerationDto.builder()
                .imageData("base64_encoded_photo")
                .build();

        when(photoGenerationService.getGenImg("10")).thenReturn(response);

        mockMvc.perform(MockMvcRequestBuilders.get("/generate-img/get/10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.image_data").value("base64_encoded_photo"));
    }

    @Test
    public void testGetGenImg_notFound() throws Exception {

        when(photoGenerationService.getGenImg("100")).thenReturn(null);

        mockMvc.perform(MockMvcRequestBuilders.get("/generate-img/get/100")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }
}
