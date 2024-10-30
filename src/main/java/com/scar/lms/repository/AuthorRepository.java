package com.scar.lms.repository;

import com.scar.lms.entity.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Integer> {

    List<Author> findByCountry(String country);

    List<Author> findByAge(Integer age);

    Optional<Author> findByEmail(String email);
}
