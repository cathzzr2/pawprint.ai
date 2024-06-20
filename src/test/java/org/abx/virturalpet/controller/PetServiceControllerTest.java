package org.abx.virturalpet.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import org.abx.virturalpet.dto.ImmutablePetServiceDto;
import org.abx.virturalpet.dto.PetServiceDto;
import org.abx.virturalpet.service.PetService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WebMvcTest(PetServiceController.class)
public class PetServiceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PetService petService;

    @BeforeEach
    public void setUp() {
        PetServiceDto petServiceDto = PetServiceDto.builder()
                .petId(1)
                .petName("Alice")
                .petType("Cat")
                .petAge("3 years")
                .build();
//        when(petService.createPet(any())).thenReturn(petServiceDto);
        when(petService.updatePet(any(), any())).thenReturn(null);
//        when(petService.deletePetByID(1)).thenReturn(true);
//        when(petService.searchPetByID(1)).thenReturn(petServiceDto);
    }

    @Test
    public void testGetPetDocument_returnOk() throws Exception {
        PetServiceDto mockedPetServiceDto = PetServiceDto.builder()
                .petId(1)
                .petName("Alice")
                .petType("Cat")
                .petAge("3 years")
                .build();

        when(petService.searchPetByID(1)).thenReturn(mockedPetServiceDto);

        String requestJsonPayload = "{\n"
                + "\"petId\": 1,\n"
                + "\"petName\": \"Alice\",\n"
                + "\"petType\": \"Cat\",\n"
                + "\"petAge\": \"3 years\",\n"
                + "}";

        mockMvc.perform(MockMvcRequestBuilders.get("/pets/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJsonPayload))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.petId").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.petName").value("Alice"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.petType").value("Cat"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.petAge").value("3 years"));
    }

    @Test
    public void testUpdatePetDocument_returnOk() throws Exception {
        ImmutablePetServiceDto mockedPetServiceDto = PetServiceDto.builder()
                .petId(1)
                .petName("Alice")
                .petType("Cat")
                .petAge("3 years")
                .build();

        when(petService.updatePet(eq(1), any())).thenReturn(mockedPetServiceDto);

        String requestJsonPayload = "{\n"
                + "\"petId\": 1,\n"
                + "\"petName\": \"Alice\",\n"
                + "\"petType\": \"Cat\",\n"
                + "\"petAge\": \"3 years\",\n"
                + "}";

        mockMvc.perform(MockMvcRequestBuilders.put("/pets/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJsonPayload))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.petId").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.petName").value("Alice"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.petType").value("Cat"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.petAge").value("3 years"));
    }

    @Test
    public void testUpdatePetDocument_returnNotFound() throws Exception {




        String requestJsonPayload = "{\n"
                + "\"petId\": 1,\n"
                + "\"petName\": \"Alice\",\n"
                + "\"petType\": \"Cat\",\n"
                + "\"petAge\": \"3 years\",\n"
                + "}";

        mockMvc.perform(MockMvcRequestBuilders.put("/pets/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJsonPayload))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void testDeletePetDocument() throws Exception {
        PetServiceDto mockedPetServiceDto = PetServiceDto.builder()
                .petId(1)
                .petName("Alice")
                .petType("Cat")
                .petAge("3 years")
                .build();

        when(petService.deletePetByID(1)).thenReturn(true);

        mockMvc.perform(MockMvcRequestBuilders.delete("/pets/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mockedPetServiceDto.toString()))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    public void testCreatePetDocument() throws Exception {
        PetServiceDto mockedPetServiceDto = PetServiceDto.builder()
                .petId(1)
                .petName("Alice")
                .petType("Cat")
                .petAge("3 years")
                .build();

        when(petService.createPet(any())).thenReturn(mockedPetServiceDto);

        String requestJsonPayload = "{\n"
                + "\"petId\": 1,\n"
                + "\"petName\": \"Alice\",\n"
                + "\"petType\": \"Cat\",\n"
                + "\"petAge\": \"3 years\",\n"
                + "}";

        mockMvc.perform(MockMvcRequestBuilders.post("/pets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJsonPayload))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.petId").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.petName").value("Alice"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.petType").value("Cat"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.petAge").value("3 years"))
                .andExpect(MockMvcResultMatchers.header().string("Location", "/pets/1"));
    }
}
