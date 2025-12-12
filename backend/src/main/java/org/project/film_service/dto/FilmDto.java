package org.project.film_service.dto;

public record FilmDto(
        Long id,
        String title,
        int year,
        String genre,
        String directorName
) {}
