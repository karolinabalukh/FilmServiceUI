package org.project.film_service.config; // Переконайтеся, що файл лежить у папці config, або змініть цей рядок

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
// ВИПРАВЛЕННЯ 1: Імпорт з папки Entity (як на скріншоті)
import org.project.film_service.Entity.Film;
// ВИПРАВЛЕННЯ 2: Імпорт з папки Repository (як на скріншоті)
import org.project.film_service.Repository.FilmRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final FilmRepository filmRepository;
    private final ObjectMapper objectMapper;

    // Конструктор
    public DataInitializer(FilmRepository filmRepository, ObjectMapper objectMapper) {
        this.filmRepository = filmRepository;
        this.objectMapper = objectMapper;
    }


    @Override
    public void run(String... args) throws Exception {
        System.out.println(">>> ПЕРЕВІРКА: DataInitializer ЗАПУСТИВСЯ! <<<"); // <--- ДОДАЙ ЦЕ

        if (filmRepository.count() == 0) {
            // ... твій код імпорту ...
        } else {
            System.out.println(">>> БАЗА НЕ ПОРОЖНЯ, ПРОПУСКАЄМО ІМПОРТ <<<"); // <--- І ЦЕ
        }
        if (filmRepository.count() == 0) {
            try {
                // Зчитуємо файл
                InputStream inputStream = TypeReference.class.getResourceAsStream("/import_films.json");

                // Перетворюємо JSON в об'єкти
                List<Film> films = objectMapper.readValue(inputStream, new TypeReference<List<Film>>(){});

                // Зберігаємо в базу
                filmRepository.saveAll(films);
                System.out.println("Фільми успішно імпортовано: " + films.size());
            } catch (IOException e) {
                System.out.println("Не вдалося імпортувати фільми: " + e.getMessage());
            }
        }
    }
}