package org.project.film_service.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="films")
public class Film {

    @Id
    @Getter @Setter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Getter
    private String title;
    @Setter
    @Getter
    private int year;
    @Setter
    @Getter
    private int duration;
    @Setter
    @Getter
    private String genre;
    @Getter
    @Setter
    private double rating;

    @Setter
    @Getter
    @Column(length = 2000)
    private String description;

    @Setter
    @Getter
    @ManyToOne
    @JoinColumn(name = "director_id")
    private Director director;

    public Film() {}

}
