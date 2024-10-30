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
@Table(name = "ADMINS")
public class Admin {

    @Id
    @GeneratedValue
    private Integer id;

    @Column(name = "NAME")
    private String name;
}