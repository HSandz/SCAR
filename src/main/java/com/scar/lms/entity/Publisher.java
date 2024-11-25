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
@Table(name = "PUBLISHERS")
public class Publisher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "NAME", length = 50, nullable = false)
    private String name;

//    @ManyToMany(fetch = FetchType.EAGER,
//            cascade = { CascadeType.PERSIST, CascadeType.MERGE },
//            mappedBy = "publishers")
//    private Set<Book> books = new HashSet<>();
}
