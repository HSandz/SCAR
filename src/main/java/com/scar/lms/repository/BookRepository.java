package com.scar.lms.repository;

import com.scar.lms.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Integer> {

    List<Book> findByTitle(String title);

    List<Book> findByAuthorId(Integer authorId);

    List<Book> findByGenreId(Integer genreId);

    List<Book> findByPublicationYear(Integer publicationYear);

    @Query("SELECT b FROM Book b WHERE b.title LIKE %?1%")
    List<Book> searchBooks(String keyword);

    Optional<Book> findByIsbn(String isbn);
}
