package org.project.film_service.Repository;

import org.project.film_service.Entity.Director;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DirectorRepository extends JpaRepository<Director, Long> {
    boolean existsByName(String name);
}
