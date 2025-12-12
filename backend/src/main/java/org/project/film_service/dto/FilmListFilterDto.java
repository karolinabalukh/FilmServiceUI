package org.project.film_service.dto;
//POST /_list
public record FilmListFilterDto(
        Long directorId,
        String genre,
        Integer year,
        int page,
        int size
) {
}
