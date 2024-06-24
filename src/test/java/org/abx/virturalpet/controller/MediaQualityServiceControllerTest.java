package org.abx.virturalpet.controller;

import static org.mockito.Mockito.when;

import org.abx.virturalpet.dto.ImprovePhotoJbDto;
import org.abx.virturalpet.dto.ImprovedPhotoResultDto;
import org.abx.virturalpet.service.MediaQualityService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WebMvcTest(MediaQualityServiceController.class)
public class MediaQualityServiceControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MediaQualityService mediaQualityService;

    @Test
    public void testImprovePhotoJbID_success() throws Exception {
        ImprovePhotoJbDto mockedResponse = ImprovePhotoJbDto.builder()
                .improvePhotoJbId("ff7fbf43-c053-4c8d-9957-0db0c4e36c72")
                .photoFile("base64_encoded_photo")
                .build();

        when(mediaQualityService.enqueuePhoto("base64_encoded_photo")).thenReturn(mockedResponse);

        String requestJsonPayload = "{\n"
                + "\"queue_jb_id\": \"ff7fbf43-c053-4c8d-9957-0db0c4e36c72\",\n"
                + "\"photo_file\": \"base64_encoded_photo\"\n"
                + "}";

        mockMvc.perform(MockMvcRequestBuilders.post("/improve")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJsonPayload))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(
                        MockMvcResultMatchers.jsonPath("$.queue_jb_id").value("ff7fbf43-c053-4c8d-9957-0db0c4e36c72"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.photo_file").value("base64_encoded_photo"));
    }

    @Test
    public void testImprovePhotoJbID_failure() throws Exception {
        when(mediaQualityService.enqueuePhoto("base64_encoded_photo")).thenReturn(null);

        String requestJsonPayload = "{\n"
                + "\"queue_jb_id\": \"ff7fbf43-c053-4c8d-9957-0db0c4e36c72\",\n"
                + "\"photoFile\": \"base64_encoded_photo\"\n"
                + "}";

        mockMvc.perform(MockMvcRequestBuilders.post("/improve")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJsonPayload))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void testGetImprovedPhoto_success() throws Exception {
        ImprovedPhotoResultDto mockedResponse = ImprovedPhotoResultDto.builder()
                .improvedPhotoUrl("http://example.com/path/to/photo/someImprovedPhotoId.jpg")
                .build();

        when(mediaQualityService.getImprovedPhoto("someImprovedPhotoId")).thenReturn(mockedResponse);

        mockMvc.perform(MockMvcRequestBuilders.get("/results/someImprovedPhotoId")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.improved_photo_url")
                        .value("http://example.com/path/to/photo/someImprovedPhotoId.jpg"));
    }

    @Test
    public void testGetImprovedPhoto_notFound() throws Exception {
        when(mediaQualityService.getImprovedPhoto("someImprovedPhotoId")).thenReturn(null);

        mockMvc.perform(MockMvcRequestBuilders.get("/results/someImprovedPhotoId")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }
}
