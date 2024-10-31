package com.scar.lms.repository;

import com.scar.lms.entity.Book;
import com.scar.lms.entity.Genre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Integer> {

    List<Book> findByTitle(String title);

    List<Book> findByAuthorName(String authorName);

    List<Book> findByGenre(Genre genre);

    List<Book> findByPublicationYear(Integer publicationYear);

    @Query("SELECT b FROM Book b WHERE b.title LIKE %?1%")
    List<Book> searchBooks(String keyword);

    Optional<Book> findByIsbn(String isbn);
}
