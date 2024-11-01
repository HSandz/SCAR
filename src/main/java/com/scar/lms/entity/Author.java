package com.scar.lms.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "AUTHORS")
public class Author {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "NAME", length = 50, nullable = false)
    private String name;

    @Column(name = "EMAIL", length = 50, unique = true)
    private String email;

    @Column(name = "AGE", length = 10)
    private int age;

    @Column(name = "COUNTRY", length = 50)
    private String country;

    @Column(name = "DESCRIPTION", length = 500)
    private String description;

    @ManyToMany(fetch = FetchType.LAZY,
            cascade = { CascadeType.PERSIST, CascadeType.MERGE },
            mappedBy = "authors")
    private Set<Book> books = new HashSet<Book>();

}