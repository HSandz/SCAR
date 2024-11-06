package com.scar.lms.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@RequiredArgsConstructor
@AllArgsConstructor
@Table(name = "USERS")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "USERNAME")
    @NonNull
    private String username;

    @Column(name = "PASSWORD")
    @NonNull
    private String password;

    @Column(name = "DISPLAY_NAME")
    @NonNull
    private String displayName;

    @Column(name = "EMAIL")
    @NonNull
    private String email;

    @Column(name = "ROLE")
    @NonNull
    private Role role;

    @Column(name = "USER_POINTS")
    @NonNull
    private Long points;

    @ManyToMany(fetch = FetchType.LAZY,
            cascade = { CascadeType.PERSIST, CascadeType.MERGE },
            mappedBy = "users")
    private Set<Book> books = new HashSet<>();

}