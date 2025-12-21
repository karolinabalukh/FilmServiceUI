package org.project.film_service.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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
    @NotBlank(message = "Ім'я режисера не може бути порожнім")
    @Size(min = 2, max = 100, message = "Ім'я має бути від 2 до 100 символів")
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
