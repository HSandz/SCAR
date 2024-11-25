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

    @Column(name = "ISBN", length = 20, nullable = false, unique = true)
    private String isbn;

    @Column(name = "TITLE", length = 100, nullable = false)
    private String title;

    @Column(name = "LANGUAGE", length = 50)
    private String language;

    @Column(name = "RATING", length = 10)
    private Double rating = 0D;

    @Column(name = "PUBLICATION_YEAR", length = 10, nullable = false)
    private Integer publicationYear;

    @Column(name = "DESCRIPTION", length = 500)
    private String description;

    @Column(name = "IMAGE_URL")
    private String imageUrl;

    @Column(name = "BORROW_COUNT")
    private int borrowCount = 0;

    @ManyToMany(fetch = FetchType.LAZY,
            cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinTable(name = "BOOK_AUTHOR",
            joinColumns = { @JoinColumn(name = "BOOK_ID") },
            inverseJoinColumns = { @JoinColumn(name = "AUTHOR_ID") })
    private Set<Author> authors = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY,
            cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinColumn(name = "GENRE_ID")
    private Genre mainGenre;

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
            cascade = { CascadeType.PERSIST, CascadeType.MERGE },
            mappedBy = "favouriteBooks")
    private Set<User> favouriteUsers = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY,
            cascade = { CascadeType.PERSIST, CascadeType.MERGE },
            mappedBy = "book")
    private Set<Borrow> borrows = new HashSet<>();

}