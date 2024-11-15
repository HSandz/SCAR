package com.scar.lms.entity;

import jakarta.persistence.*;
import lombok.NonNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "BORROWS")
public class Borrow {

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
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY,
            cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinColumn(name = "book_id")
    private Book book;
}
