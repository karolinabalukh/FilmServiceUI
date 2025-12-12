package org.project.film_service.Service;

import lombok.RequiredArgsConstructor;
import org.project.film_service.Entity.Director;
import org.project.film_service.Repository.DirectorRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DirectorServiceImpl implements DirectorService {
    private final DirectorRepository directorRepository;

    @Override
    public Director createDirector(Director director) {
        if(directorRepository.existsByName(director.getName())) {
            throw new IllegalArgumentException("Director with name '" + director.getName() + "' already exists");
        }
        return directorRepository.save(director);
    }

    @Override
    public Director updateDirector(Long id, Director newData) {
        Director exist = getById(id);
        if(!exist.getName().equals(newData.getName()) && directorRepository.existsByName(newData.getName())) {
            throw new IllegalArgumentException("Director with name '" + newData.getName() + "' already exists");
        }
        exist.setName(newData.getName());
        exist.setBirthDate(newData.getBirthDate());
        exist.setCountry(newData.getCountry());
        return directorRepository.save(exist);
    }

    @Override
    public void deleteDirector(Long id) {
        if(!directorRepository.existsById(id)) {
            throw new IllegalArgumentException("Director with id '" + id + "' does not exist");
        }
        directorRepository.deleteById(id);
    }

    @Override
    public List<Director> getAllDirectors() {
        return directorRepository.findAll();
    }

    @Override
    public Director getById(Long id) {
        return directorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Director with id '" + id + "' not found"));
    }
}