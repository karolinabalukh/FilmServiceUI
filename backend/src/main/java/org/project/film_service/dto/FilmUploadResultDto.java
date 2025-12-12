package org.project.film_service.dto;

public record FilmUploadResultDto(
        int successCount,
        int failedCount
) {
}
