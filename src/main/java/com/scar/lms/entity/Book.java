package com.scar.lms.entity;

import jakarta.persistence.*;

import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "BOOKS")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "ISBN", length = 20, nullable = false, unique = true)
    private String isbn;

    @Column(name = "TITLE", length = 100, nullable = false)
    private String title;

    @Column(name = "LANGUAGE", length = 50)
    private String language;

    @Column(name = "RATING", length = 10, nullable = false)
    private Double rating;

    @Column(name = "PUBLICATION_YEAR", length = 10, nullable = false)
    private Integer publicationYear;

    @Column(name = "DESCRIPTION", length = 500)
    private String description;

    @Column(name = "IMAGE_URL")
    private String imageUrl;

    @ManyToMany(fetch = FetchType.LAZY,
            cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinTable(name = "BOOK_AUTHOR",
            joinColumns = { @JoinColumn(name = "BOOK_ID") },
            inverseJoinColumns = { @JoinColumn(name = "AUTHOR_ID") })
    private Set<Author> authors = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY,
            cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinTable(name = "BOOK_GENRE",
            joinColumns = { @JoinColumn(name = "BOOK_ID") },
            inverseJoinColumns = { @JoinColumn(name = "GENRE_ID") })
    private Set<Genre> genres = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY,
            cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinTable(name = "BOOK_PUBLISHER",
            joinColumns = { @JoinColumn(name = "BOOK_ID") },
            inverseJoinColumns = { @JoinColumn(name = "PUBLISHER_ID") })
    private Set<Publisher> publishers = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY,
            cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinTable(name = "BOOK_USER",
            joinColumns = { @JoinColumn(name = "BOOK_ID") },
            inverseJoinColumns = { @JoinColumn(name = "USER_ID") })
    private Set<User> users = new HashSet<>();
}