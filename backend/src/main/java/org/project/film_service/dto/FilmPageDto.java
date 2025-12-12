package org.project.film_service.dto;

import java.util.List;

public record FilmPageDto(
        List<FilmDto> list,
        int totalPages,
        long totalElements
) {}