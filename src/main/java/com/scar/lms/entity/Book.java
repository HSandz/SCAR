package com.scar.lms.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

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
    @NonNull
    private String title;

    @Column(name = "LANGUAGE", length = 50)
    private String language;

    @Column(name = "RATING", length = 10)
    private Double rating = 0D;

    @Column(name = "PUBLICATION_YEAR", length = 10)
    private Integer publicationYear;

    @Column(name = "DESCRIPTION", columnDefinition = "LONGTEXT")
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
            mappedBy = "favouriteBooks")
    private Set<User> favouriteUsers = new HashSet<>();

    @OneToMany(fetch = FetchType.EAGER,
            mappedBy = "book")
    private Set<Borrow> borrows = new HashSet<>();

    @OneToMany(fetch = FetchType.EAGER,
            mappedBy = "book")
    private Set<Rating> ratings = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return id == book.id;
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy
                ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode()
                : getClass().hashCode();
    }
}