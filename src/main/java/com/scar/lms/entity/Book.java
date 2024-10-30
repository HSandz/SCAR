package com.scar.lms.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "BOOKS")
public class Book {

    @Id
    @GeneratedValue
    private Integer id;

    @Column(name = "TITLE")
    private String title;

    @Column(name = "AUTHOR_NAME")
    private String authorName;

    @Column(name = "GENRE")
    private Genre genre;

    @Column(name = "ISBN")
    private String isbn;

    @Column(name = "PUBLICATION_YEAR")
    private Integer publicationYear;

    @Column(name = "DESCRIPTION")
    private String description;
}