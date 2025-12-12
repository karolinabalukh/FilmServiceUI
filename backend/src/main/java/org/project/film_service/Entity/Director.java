package org.project.film_service.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "directors", uniqueConstraints = {@UniqueConstraint(columnNames = "name")})
public class Director {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Getter
    @NotBlank(message = "Name cannot be empty")
    private String name;
    @Getter
    @Setter
    private String birthDate;
    @Setter
    @Getter
    private String country;

    public Director() {
        // потрібен для Jackson
    }

}
