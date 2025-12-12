package org.project.film_service;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.project.film_service.Entity.Director;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class DirectorIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    //GET
    @Test
    void getAllDirectors_shouldReturnList() throws Exception {
        mockMvc.perform(get("/api/directors"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)));
    }

    //POST
    @Test
    void createDirector_shouldCreate() throws Exception {
        Director newDirector = new Director();
        newDirector.setName("New Director");
        newDirector.setBirthDate("01-01-1990");
        newDirector.setCountry("Ukraine");

        mockMvc.perform(post("/api/directors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newDirector)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("New Director"));
    }

    //PUT
    @Test
    void updateDirector_shouldUpdate() throws Exception {
        Director updateData = new Director();
        updateData.setName("Updated Nolan");
        updateData.setBirthDate("1970");
        updateData.setCountry("USA");

        mockMvc.perform(put("/api/directors/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateData)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Nolan"));
    }

    // DELETE
    @Test
    void deleteDirector_shouldDelete() throws Exception {
        Director tempDirector = new Director();
        tempDirector.setName("To Delete");

        String response = mockMvc.perform(post("/api/directors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tempDirector)))
                .andReturn().getResponse().getContentAsString();

        String idStr = response.split("\"id\":")[1].split(",")[0];

        mockMvc.perform(delete("/api/directors/" + idStr))
                .andExpect(status().isNoContent());
    }
}
