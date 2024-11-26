package com.scar.lms.entity;

import jakarta.persistence.*;

import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "BOOKS")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "ISBN", length = 20)
    private String isbn;

    @Column(name = "TITLE", length = 100, nullable = false)
    private String title;

    @Column(name = "LANGUAGE", length = 50)
    private String language;

    @Column(name = "RATING", length = 10)
    private Double rating = 0D;

    @Column(name = "PUBLICATION_YEAR", length = 10, nullable = false)
    private Integer publicationYear;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "IMAGE_URL")
    private String imageUrl;

    @Column(name = "BORROW_COUNT")
    private int borrowCount = 0;

    @Column(name = "AUTHOR")
    private String author;

    @Column(name = "GENRE")
    private String genre;

    @Column(name = "PUBLISHER")
    private String publisher;

    @ManyToMany(fetch = FetchType.EAGER,
            cascade = { CascadeType.MERGE },
            mappedBy = "favouriteBooks")
    private Set<User> favouriteUsers = new HashSet<>();

    @OneToMany(fetch = FetchType.EAGER,
            cascade = { CascadeType.MERGE },
            mappedBy = "book")
    private Set<Borrow> borrows = new HashSet<>();
}