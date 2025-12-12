package org.project.film_service.Service;
import org.project.film_service.Entity.Director;

import java.util.List;

public interface DirectorService {
    Director createDirector(Director director);
    Director updateDirector(Long id, Director newData);
    void deleteDirector(Long id);
    List<Director> getAllDirectors();
    Director getById(Long id);
}
