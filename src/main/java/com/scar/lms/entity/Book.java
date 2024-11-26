package com.scar.lms.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.proxy.HibernateProxy;

import java.util.HashSet;
import java.util.Objects;
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
            cascade = { CascadeType.PERSIST, CascadeType.MERGE },
            mappedBy = "favouriteBooks")
    private Set<User> favouriteUsers = new HashSet<>();

    @OneToMany(fetch = FetchType.EAGER,
            cascade = { CascadeType.PERSIST, CascadeType.MERGE },
            mappedBy = "book")
    private Set<Borrow> borrows = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return id == book.id &&
                isbn.equals(book.isbn) &&
                title.equals(book.title);
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy
                ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode()
                : getClass().hashCode();
    }
}