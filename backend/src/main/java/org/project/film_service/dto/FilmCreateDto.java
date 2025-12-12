package org.project.film_service.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

//POST
public record FilmCreateDto(
        @NotBlank(message = "Title is required")
        String title,

        @Min(value = 1888, message = "Year must be no less than 1888")
        int year,

        @Positive(message = "Duration must be positive")
        int duration,

        @NotBlank(message = "Genre is required")
        String genre,

        @Min(0)
        double rating,

        String description,

        @NotNull(message = "Director ID is required")
        Long directorId
) {
}
