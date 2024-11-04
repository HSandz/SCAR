package com.scar.lms.repository;

import com.scar.lms.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Integer>, JpaSpecificationExecutor<Book> {

    List<Book> findByTitle(String title);

    List<Book> findByPublicationYear(int publicationYear);

    Optional<Book> findByIsbn(String isbn);

    @Query("SELECT b FROM Book b WHERE b.title LIKE %?1%")
    List<Book> searchBooks(String keyword);

    @Query("SELECT b FROM Book b JOIN Author a WHERE a.id = ?1")
    List<Book> findByAuthor(int authorId);

    @Query("SELECT b FROM Book b JOIN Genre g WHERE g.id = ?1")
    List<Book> findByGenre(int genreId);

    @Query("SELECT b FROM Book b JOIN Publisher p WHERE p.id = ?1")
    List<Book> findByPublisher(int publisherId);

}
