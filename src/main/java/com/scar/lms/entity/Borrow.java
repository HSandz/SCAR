package com.scar.lms.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "BORROWS")
public class Borrow {

    public static final int MAXIMUM_BORROW_DAY = 150;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "BORROW_DATE", nullable = false)
    @NonNull
    private LocalDate borrowDate;

    @Column(name = "RETURN_DATE", nullable = false)
    @NonNull
    private LocalDate returnDate;

    @ManyToOne(fetch = FetchType.LAZY,
            cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinColumn(name = "USER_ID")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY,
            cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinColumn(name = "BOOK_ID")
    private Book book;
}
