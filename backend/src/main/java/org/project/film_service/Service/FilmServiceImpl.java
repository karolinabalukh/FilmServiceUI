package org.project.film_service.Service;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.Predicate;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.project.film_service.Entity.Director;
import org.project.film_service.Entity.Film;
import org.project.film_service.Repository.DirectorRepository;
import org.project.film_service.Repository.FilmRepository;
import org.project.film_service.dto.FilmCreateDto;
import org.project.film_service.dto.FilmListFilterDto;
import org.project.film_service.dto.FilmUploadResultDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FilmServiceImpl implements FilmService {

    private final FilmRepository filmRepository;
    private final DirectorRepository directorRepository;
    private final ObjectMapper objectMapper;

    private void mapDtoToEntity(FilmCreateDto dto, Film film, Director director) {
        film.setTitle(dto.title());
        film.setYear(dto.year());
        film.setDuration(dto.duration());
        film.setGenre(dto.genre());
        film.setRating(dto.rating());
        film.setDescription(dto.description());
        film.setDirector(director);
    }

    private Specification<Film> createSpecific(FilmListFilterDto filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (filter.directorId() != null) {
                predicates.add(cb.equal(root.get("director").get("id"), filter.directorId()));
            }
            if (filter.genre() != null && !filter.genre().isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("genre")), "%" + filter.genre().toLowerCase() + "%"));
            }
            if (filter.year() != null) {
                predicates.add(cb.equal(root.get("year"), filter.year()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private String escapeCsv(String data) {
        if (data == null) return "";
        if (data.contains(",") || data.contains("\"") || data.contains("\n")) {
            return "\"" + data.replace("\"", "\"\"") + "\"";
        }
        return data;
    }

    @Override
    @Transactional
    public Film create(FilmCreateDto dto) {
        Director director = directorRepository.findById(dto.directorId())
                .orElseThrow(() -> new EntityNotFoundException("Director not found with id: " + dto.directorId()));
        Film film = new Film();
        mapDtoToEntity(dto, film, director);
        return filmRepository.save(film);
    }

    @Override
    @Transactional
    public Film update(Long id, FilmCreateDto dto) {
        Film film = getById(id);
        Director director = directorRepository.findById(dto.directorId())
                .orElseThrow(() -> new EntityNotFoundException("Director not found with id: " + dto.directorId()));
        mapDtoToEntity(dto, film, director);
        return filmRepository.save(film);
    }

    @Override
    public Film getById(Long id) {
        return filmRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Film not found with id: " + id));
    }

    @Override
    public void delete(Long id) {
        if (!filmRepository.existsById(id)) {
            throw new EntityNotFoundException("Film not found with id: " + id);
        }
        filmRepository.deleteById(id);
    }

    @Override
    public Page<Film> list(FilmListFilterDto filter) {
        Pageable pageable = PageRequest.of(filter.page(), filter.size());
        Specification<Film> specification = createSpecific(filter);
        return filmRepository.findAll(specification, pageable);
    }

    @Override
    public byte[] generateReport(FilmListFilterDto filter) {
        Specification<Film> specification = createSpecific(filter);
        List<Film> films = filmRepository.findAll(specification);
        StringBuilder csv = new StringBuilder();
        csv.append("ID,Title,Year,Genre,Rating,Director\n");
        for (Film film : films) {
            csv.append(film.getId()).append(",")
                    .append(escapeCsv(film.getTitle())).append(",")
                    .append(film.getYear()).append(",")
                    .append(escapeCsv(film.getGenre())).append(",")
                    .append(film.getRating()).append(",")
                    .append(escapeCsv(film.getDirector().getName())).append("\n");
        }
        return csv.toString().getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public FilmUploadResultDto uploadJson(InputStream jsonStream) {
        int success = 0;
        int failure = 0;

        try (JsonParser parser = objectMapper.createParser(jsonStream)) {

            // Перевіряємо, чи файл починається як масив (з дужки [ )
            if (parser.nextToken() == com.fasterxml.jackson.core.JsonToken.START_ARRAY) {

                // Читаємо поки масив не закінчиться ( ] )
                while (parser.nextToken() != com.fasterxml.jackson.core.JsonToken.END_ARRAY) {
                    try {
                        // Читаємо один об'єкт фільму
                        FilmCreateDto dto = objectMapper.readValue(parser, FilmCreateDto.class);
                        create(dto); // Пробуємо зберегти в базу
                        success++;
                    } catch (Exception e) {
                        failure++;
                        System.err.println("Failed to import item: " + e.getMessage());
                    }
                }
            }
            // Якщо файл не масив, а просто набір об'єктів (NDJSON)
            else {
                MappingIterator<FilmCreateDto> iterator = objectMapper.readerFor(FilmCreateDto.class).readValues(parser);
                while (iterator.hasNext()) {
                    try {
                        FilmCreateDto dto = iterator.next();
                        create(dto);
                        success++;
                    } catch (Exception e) {
                        failure++;
                    }
                }
            }

        } catch (IOException e) {
            System.err.println("JSON parsing error: " + e.getMessage());
        }
        return new FilmUploadResultDto(success, failure);
    }
}