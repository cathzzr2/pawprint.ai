package org.abx.virturalpet.controller;

import static org.mockito.Mockito.when;

import java.util.UUID;
import org.abx.virturalpet.dto.ImmutablePetServiceDto;
import org.abx.virturalpet.dto.PetServiceDto;
import org.abx.virturalpet.dto.PetTypeEnum;
import org.abx.virturalpet.service.PetService;
import org.junit.jupiter.api.Test;
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

    private UUID samplePetId = UUID.randomUUID();

    @Test
    public void testGetPetDocument_returnOk() throws Exception {
        PetServiceDto mockedPetServiceDto = PetServiceDto.builder()
                .petId(samplePetId)
                .petName("Alice")
                .petType(PetTypeEnum.CAT)
                .petAge(3)
                .build();

        when(petService.searchPetByID(samplePetId)).thenReturn(mockedPetServiceDto);

        String requestJsonPayload = "{\n"
                + "\"pet_id\": \"" + samplePetId.toString() + "\",\n"
                + "\"pet_name\": \"Alice\",\n"
                + "\"pet_type\": \"CAT\",\n"
                + "\"pet_age\": 3\n"
                + "}";

        mockMvc.perform(MockMvcRequestBuilders.get("/pets/{petId}", samplePetId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJsonPayload))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.pet_id").value(samplePetId.toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.pet_name").value("Alice"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.pet_type").value("CAT"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.pet_age").value(3));
    }

    @Test
    public void testGetPetDocument_returnNotFound() throws Exception {
        String requestJsonPayload = "{\n"
                + "\"pet_id\": \"" + samplePetId.toString() + "\",\n"
                + "\"pet_name\": \"Alice\",\n"
                + "\"pet_type\": \"CAT\",\n"
                + "\"pet_age\": 3\n"
                + "}";

        mockMvc.perform(MockMvcRequestBuilders.get("/pets/{petId}", samplePetId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJsonPayload))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void testUpdatePetDocument_returnOk() throws Exception {
        ImmutablePetServiceDto mockedPetServiceDto = PetServiceDto.builder()
                .petId(samplePetId)
                .petName("Alice")
                .petType(PetTypeEnum.CAT)
                .petAge(3)
                .build();

        when(petService.updatePet(org.mockito.ArgumentMatchers.eq(samplePetId), org.mockito.ArgumentMatchers.any()))
                .thenReturn(mockedPetServiceDto);

        String requestJsonPayload = "{\n"
                + "\"pet_id\": \"" + samplePetId.toString() + "\",\n"
                + "\"pet_name\": \"Alice\",\n"
                + "\"pet_type\": \"CAT\",\n"
                + "\"pet_age\": 3\n"
                + "}";

        mockMvc.perform(MockMvcRequestBuilders.put("/pets/{petId}", samplePetId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJsonPayload))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.pet_id").value(samplePetId.toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.pet_name").value("Alice"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.pet_type").value("CAT"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.pet_age").value(3));
    }

    @Test
    public void testUpdatePetDocument_returnNotFound() throws Exception {

        String requestJsonPayload = "{\n"
                + "\"pet_id\": \"" + samplePetId.toString() + "\",\n"
                + "\"pet_name\": \"Alice\",\n"
                + "\"pet_type\": \"CAT\",\n"
                + "\"pet_age\": 3\n"
                + "}";

        mockMvc.perform(MockMvcRequestBuilders.put("/pets/{petId}", samplePetId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJsonPayload))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void testDeletePetDocument() throws Exception {
        PetServiceDto mockedPetServiceDto = PetServiceDto.builder()
                .petId(samplePetId)
                .petName("Alice")
                .petType(PetTypeEnum.CAT)
                .petAge(3)
                .build();

        when(petService.deletePetByID(samplePetId)).thenReturn(true);

        mockMvc.perform(MockMvcRequestBuilders.delete("/pets/{petId}", samplePetId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mockedPetServiceDto.toString()))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    public void testDeletePetDocument_returnNotFound() throws Exception {
        PetServiceDto mockedPetServiceDto = PetServiceDto.builder()
                .petId(samplePetId)
                .petName("Alice")
                .petType(PetTypeEnum.CAT)
                .petAge(3)
                .build();

        when(petService.deletePetByID(samplePetId)).thenReturn(false);

        mockMvc.perform(MockMvcRequestBuilders.delete("/pets/{petId}", samplePetId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mockedPetServiceDto.toString()))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void testCreatePetDocument() throws Exception {
        PetServiceDto mockedPetServiceDto = PetServiceDto.builder()
                .petId(samplePetId)
                .petName("Alice")
                .petType(PetTypeEnum.CAT)
                .petAge(3)
                .build();

        when(petService.createPet(org.mockito.ArgumentMatchers.any())).thenReturn(mockedPetServiceDto);

        String requestJsonPayload = "{\n"
                + "\"pet_id\": \"" + samplePetId.toString() + "\",\n"
                + "\"pet_name\": \"Alice\",\n"
                + "\"pet_type\": \"CAT\",\n"
                + "\"pet_age\": 3\n"
                + "}";

        mockMvc.perform(MockMvcRequestBuilders.post("/pets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJsonPayload))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.pet_id").value(samplePetId.toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.pet_name").value("Alice"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.pet_type").value("CAT"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.pet_age").value(3))
                .andExpect(MockMvcResultMatchers.header().string("Location", "/pets/" + samplePetId));
    }

    @Test
    public void testCreatePetDocument_returnBadRequest() throws Exception {

        when(petService.createPet(org.mockito.ArgumentMatchers.any())).thenReturn(null);

        String requestJsonPayload = "{\n"
                + "\"pet_id\": \"" + samplePetId.toString() + "\",\n"
                + "\"pet_name\": \"Alice\",\n"
                + "\"pet_type\": \"CAT\",\n"
                + "\"pet_age\": 3\n"
                + "}";

        mockMvc.perform(MockMvcRequestBuilders.post("/pets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJsonPayload))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }
}
