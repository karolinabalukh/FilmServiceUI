package org.project.film_service;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.project.film_service.dto.FilmCreateDto;
import org.project.film_service.dto.FilmListFilterDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class FilmIntegrationTest {

	@Autowired
	private MockMvc mockMvc; //віртуальний Postman
	@Autowired
	private ObjectMapper objectMapper;

	//POST /api/films
	@Test
	void createFilm_shouldReturnCreatedFilm() throws Exception {
		FilmCreateDto newFilm = new FilmCreateDto(
				"Integration Test Movie", 2024, 120, "Testing", 9.9, "Description", 1L
		);

		mockMvc.perform(post("/api/films")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(newFilm)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").exists())
				.andExpect(jsonPath("$.title").value("Integration Test Movie"))
				.andExpect(jsonPath("$.director.name").value("Christopher Nolan"));
	}

	//GET /api/films/{id}
	@Test
	void getFilmById_shouldReturnFilm() throws Exception {
		String responseJson = mockMvc.perform(post("/api/films")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(new FilmCreateDto(
								"To Find", 2022, 100, "Comedy", 7.0, "Desc", 1L))))
				.andReturn().getResponse().getContentAsString();
		String createdId = responseJson.split("\"id\":")[1].split(",")[0];
		mockMvc.perform(get("/api/films/" + createdId))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.title").value("To Find"));
	}

	//PUT /api/films/{id})
	@Test
	void updateFilm_shouldUpdateData() throws Exception {
		String responseJson = mockMvc.perform(post("/api/films")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(new FilmCreateDto(
								"Original Title", 2022, 100, "Comedy", 7.0, "Desc", 1L))))
				.andReturn().getResponse().getContentAsString();
		String createdId = responseJson.split("\"id\":")[1].split(",")[0];

		FilmCreateDto updateDto = new FilmCreateDto(
				"Updated Title", 2025, 110, "Drama", 9.0, "New Desc", 1L);
		mockMvc.perform(put("/api/films/" + createdId)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(updateDto)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.title").value("Updated Title"));
	}

	// DELETE /api/films/{id})
	@Test
	void deleteFilm_shouldRemoveFilm() throws Exception {
		String responseJson = mockMvc.perform(post("/api/films")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(new FilmCreateDto(
								"To Delete", 2022, 100, "Comedy", 7.0, "Desc", 1L))))
				.andReturn().getResponse().getContentAsString();
		String createdId = responseJson.split("\"id\":")[1].split(",")[0];

		mockMvc.perform(delete("/api/films/" + createdId))
				.andExpect(status().isNoContent());
		mockMvc.perform(get("/api/films/" + createdId))
				.andExpect(status().isNotFound());
	}

	//POST /api/films/_list
	@Test
	void listFilms_shouldReturnPage() throws Exception {
		FilmListFilterDto filter = new FilmListFilterDto(1L, null, null, 0, 10);

		mockMvc.perform(post("/api/films/_list")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(filter)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.list").isArray())
				.andExpect(jsonPath("$.totalElements").exists());
	}

	//POST /api/films/_report
	@Test
	void report_shouldReturnCsv() throws Exception {
		FilmListFilterDto filter = new FilmListFilterDto(1L, null, null, 0, 10);

		mockMvc.perform(post("/api/films/_report")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(filter)))
				.andExpect(status().isOk())
				.andExpect(content().contentType("text/csv"))
				.andExpect(header().string("Content-Disposition", containsString("films_report.csv")));
	}

	//POST /api/films/upload
	@Test
	void upload_shouldParseJson() throws Exception {
		String jsonContent = """
            [
              {
                "title": "Imported Movie",
                "year": 2024,
                "duration": 120,
                "genre": "Test",
                "rating": 5.0,
                "description": "Desc",
                "directorId": 1
              }
            ]
            """;
		MockMultipartFile file = new MockMultipartFile(
				"file",
				"import.json",
				"application/json",
				jsonContent.getBytes()
		);
		mockMvc.perform(multipart("/api/films/upload").file(file))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.successCount").value(1));
	}
}