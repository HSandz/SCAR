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
@Table(name = "USERS")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "USERNAME", nullable = false, unique = true)
    @NonNull
    private String username;

    @Column(name = "PASSWORD")
    private String password;

    @Column(name = "DISPLAY_NAME")
    @NonNull
    private String displayName;

    @Column(name = "EMAIL", unique = true)
    private String email;

    @Column(name = "ROLE", nullable = false)
    @NonNull
    private Role role;

    @Column(name = "USER_POINTS")
    @NonNull
    private Long points = 0L;

    @Column(name = "PROFILE_PICTURE_URL")
    private String profilePictureUrl;

    @OneToMany(fetch = FetchType.LAZY,
            cascade = { CascadeType.PERSIST, CascadeType.MERGE },
            mappedBy = "user")
    private Set<Borrow> borrows = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY,
            cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinTable(name = "FAVOURITES",
            joinColumns = { @JoinColumn(name = "USER_ID") },
            inverseJoinColumns = { @JoinColumn(name = "BOOK_ID") })
    private Set<Book> favouriteBooks = new HashSet<>();

}