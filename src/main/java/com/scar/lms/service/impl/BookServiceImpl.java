package com.scar.lms.service.impl;

import com.scar.lms.entity.Book;
import com.scar.lms.exception.DuplicateResourceException;
import com.scar.lms.exception.ResourceNotFoundException;
import com.scar.lms.repository.BookRepository;
import com.scar.lms.repository.specification.BookSpecification;
import com.scar.lms.service.BookService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;

    public BookServiceImpl(final BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    @Override
    public List<Book> findAllBooks() {
        return bookRepository.findAll();
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    @Override
    public List<Book> findBooksByTitle(String title) {
        return bookRepository.findByTitle(title);
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    @Override
    public List<Book> findBooksByPublicationYear(Integer year) {
        return bookRepository.findByPublicationYear(year);
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    @Override
    public List<Book> searchBooks(String keyword) {
        return bookRepository.searchBooks(keyword);
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    @Override
    public List<Book> findBooksByAuthor(int authorId) {
        return bookRepository.findByAuthor(authorId);
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    @Override
    public List<Book> findBooksByGenre(int genreId) {
        return bookRepository.findByGenre(genreId);
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    @Override
    public List<Book> findBooksByPublisher(int publisherId) {
        return bookRepository.findByPublisher(publisherId);
    }

    @Override
    public Page<Book> findPaginated(Pageable pageable) {
        return bookRepository.findAll(pageable);
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    @Override
    public Page<Book> findFilteredAndPaginated(String title,
                                               String authorName,
                                               String genreName,
                                               String publisherName,
                                               Integer year,
                                               Pageable pageable) {

        Specification<Book> spec = Specification.where(null);

        if (title != null) {
            spec = spec.and(BookSpecification.hasTitle(title));
        }
        if (authorName != null) {
            spec = spec.and(BookSpecification.hasAuthor(authorName));
        }
        if (genreName != null) {
            spec = spec.and(BookSpecification.hasGenre(genreName));
        }
        if (publisherName != null) {
            spec = spec.and(BookSpecification.hasPublisher(publisherName));
        }
        if (year != null) {
            spec = spec.and(BookSpecification.hasYear(year));
        }

        return bookRepository.findAll(spec, pageable);
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    @Override
    public Book findBookById(int id) {
        return bookRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book with id not found: " + id));
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    @Override
    public Book findBookByIsbn(String isbn) {
        return bookRepository
                .findByIsbn(isbn)
                .orElseThrow(() -> new ResourceNotFoundException("Book with isbn not found: " + isbn));
    }

    @Override
    public void addBook(Book book) {
        if (bookRepository.findByIsbn(book.getIsbn()).isPresent()) {
            throw new DuplicateResourceException("Book with ISBN " + book.getIsbn() + " already exists");
        }
        bookRepository.save(book);
    }

    @Override
    public void updateBook(Book book) {
        if (!bookRepository.existsById(book.getId())) {
            throw new ResourceNotFoundException("Book with id not found: " + book.getId());
        }
        bookRepository.save(book);
    }

    @Override
    public void deleteBook(int id) {
        Book book = bookRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book with id not found: " + id));
        bookRepository.delete(book);
    }
}
