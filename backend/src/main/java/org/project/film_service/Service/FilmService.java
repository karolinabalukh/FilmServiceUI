package org.project.film_service.Service;
import org.project.film_service.Entity.Film;
import org.project.film_service.dto.FilmCreateDto;
import org.project.film_service.dto.FilmListFilterDto;
import org.project.film_service.dto.FilmUploadResultDto;
import org.springframework.data.domain.Page;
import java.io.InputStream;

public interface FilmService {
    Film create(FilmCreateDto dto);
    Film update(Long id, FilmCreateDto dto);
    Film getById(Long id);
    void delete(Long id);
    Page<Film> list(FilmListFilterDto filter);
    byte[] generateReport(FilmListFilterDto filter);
    FilmUploadResultDto uploadJson(InputStream jsonStream);
}