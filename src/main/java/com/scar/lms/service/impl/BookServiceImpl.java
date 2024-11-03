package com.scar.lms.service.impl;

import com.scar.lms.entity.Book;
import com.scar.lms.exception.NotFoundException;
import com.scar.lms.repository.BookRepository;
import com.scar.lms.service.BookService;

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
    public List<Book> findBooksByPublicationYear(int year) {
        return bookRepository.findByPublicationYear(year);
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    @Override
    public List<Book> searchBook(String keyword) {
        return bookRepository.searchBooks(keyword);
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
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
    public void createBook(Book book) {
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

}
