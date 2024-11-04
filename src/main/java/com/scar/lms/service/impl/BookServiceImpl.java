package com.scar.lms.service.impl;

import com.scar.lms.entity.Book;
import com.scar.lms.exception.NotFoundException;
import com.scar.lms.repository.BookRepository;
import com.scar.lms.repository.specification.BookSpecification;
import com.scar.lms.service.BookService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
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
    public List<Book> findBooksByPublicationYear(int year) {
        return bookRepository.findByPublicationYear(year);
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    @Override
    public List<Book> searchBooks(String keyword) {
        return bookRepository.searchBooks(keyword);
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    @Override
    public List<Book> filterBooks(String title, String authorName, String genreName, String publisherName, Integer year) {

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
        
        return bookRepository.findAll(spec);
    }

    @Override
    public List<Book> findBooksByAuthor(int authorId) {
        return bookRepository.findByAuthor(authorId);
    }

    @Override
    public List<Book> findBooksByGenre(int genreId) {
        return bookRepository.findByGenre(genreId);
    }

    @Override
    public List<Book> findBooksByPublisher(int publisherId) {
        return bookRepository.findByPublisher(publisherId);
    }

    @Override
    public Book findBookById(int id) {
        return bookRepository
                .findById(id)
                .orElseThrow(
                        () -> new NotFoundException(
                                String.format("Book with id %d not found", id)
                        )
                );
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    @Override
    public Book findBookByIsbn(String isbn) {
        return bookRepository
                .findByIsbn(isbn)
                .orElseThrow(
                        () -> new NotFoundException(
                                String.format("Book with isbn %s not found", isbn)
                        )
                );
    }

    @Override
    public void addBook(Book book) {
        bookRepository.save(book);
    }

    @Override
    public void updateBook(Book book) {
        bookRepository.save(book);
    }

    @Override
    public void deleteBook(int id) {
        var book = bookRepository
                .findById(id)
                .orElseThrow(
                        () -> new NotFoundException(
                                String.format("Book with id %d not found", id)
                        )
                );
        bookRepository.delete(book);
    }

    @Override
    public Page<Book> findPaginated(Pageable pageable) {
        long startTime = System.currentTimeMillis();
        List<Book> allBooks = findAllBooks();
        int pageSize = pageable.getPageSize();
        int currentPage = pageable.getPageNumber();
        int startItem = currentPage * pageSize;
        List<Book> list;

        if (allBooks.size() < startItem) {
            list = Collections.emptyList();
        } else {
            int toIndex = Math.min(startItem + pageSize, allBooks.size());
            list = allBooks.subList(startItem, toIndex);
        }

        var bookPage = new PageImpl<>(list, PageRequest.of(currentPage, pageSize), allBooks.size());
        long endTime = System.currentTimeMillis();
        System.out.printf("Method execution time: %d ms\n", endTime - startTime);
        return bookPage;
    }

}
