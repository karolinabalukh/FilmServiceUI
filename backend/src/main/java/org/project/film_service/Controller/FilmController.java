package org.project.film_service.Controller;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.project.film_service.Entity.Film;
import org.project.film_service.Service.FilmService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.project.film_service.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/films")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class FilmController {
    private final FilmService filmService;

    @PostMapping
    public ResponseEntity<Film> createFilm(@RequestBody @Valid FilmCreateDto dto) {
        return ResponseEntity.ok(filmService.create(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Film> getById(@PathVariable Long id) {
        return ResponseEntity.ok(filmService.getById(id));
    }

    @PutMapping("/{id}")
    // ВИПРАВЛЕНО: Прибрана зайва дужка
    public ResponseEntity<Film> update(@PathVariable Long id, @RequestBody @Valid FilmCreateDto dto) {
        return ResponseEntity.ok(filmService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        filmService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/_list")
    public ResponseEntity<FilmPageDto> list(@RequestBody FilmListFilterDto filmFilter) {
        Page<Film> filmPage = filmService.list(filmFilter);
        List<FilmDto> dtoList = filmPage.getContent().stream()
                .map(f -> new FilmDto(
                        f.getId(),
                        f.getTitle(),
                        f.getYear(),
                        f.getGenre(),
                        f.getDirector().getName()))
                .toList();

        return ResponseEntity.ok(new FilmPageDto(dtoList, filmPage.getTotalPages(), filmPage.getTotalElements()));
    }

    @PostMapping("/_report")
    public ResponseEntity<byte[]> report(@RequestBody FilmListFilterDto filter) {
        byte[] csvData = filmService.generateReport(filter);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=films_report.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csvData);
    }

    @PostMapping("/upload")
    public ResponseEntity<FilmUploadResultDto> upload(@RequestParam("file") MultipartFile file) throws IOException {
        return ResponseEntity.ok(filmService.uploadJson(file.getInputStream()));
    }

    @GetMapping
    public Page<Film> getAllFilms(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Integer year
    ) {
        return filmService.getFilms(title, year, page, size);
    }
}