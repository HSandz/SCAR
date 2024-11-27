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
    private long points = 0;

    @Column(name = "PROFILE_PICTURE_URL")
    private String profilePictureUrl;

    @Column(name = "ABOUT_ME")
    private String aboutMe;

    @OneToMany(fetch = FetchType.EAGER,
            mappedBy = "user")
    private Set<Borrow> borrows = new HashSet<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "FAVOURITES",
            joinColumns = { @JoinColumn(name = "USER_ID") },
            inverseJoinColumns = { @JoinColumn(name = "BOOK_ID") })
    private Set<Book> favouriteBooks = new HashSet<>();

    @OneToMany(fetch = FetchType.EAGER,
            mappedBy = "user")
    private Set<Notify> notifies = new HashSet<>();

    @OneToMany(fetch = FetchType.EAGER,
            mappedBy = "user")
    private Set<Rating> ratings = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id == user.id;
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy
                ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode()
                : getClass().hashCode();
    }
}