package com.scar.lms.service.impl;

import com.scar.lms.entity.Book;
import com.scar.lms.exception.DuplicateResourceException;
import com.scar.lms.exception.ResourceNotFoundException;
import com.scar.lms.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BookServiceImplTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookServiceImpl bookService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindAllBooks() {
        List<Book> books = List.of(new Book(), new Book());
        when(bookRepository.findAll()).thenReturn(books);

        //List<Book> result = bookService.findAllBooks();
        //assertEquals(books, result);
    }

    @Test
    void testFindBooksByTitle() {
        List<Book> books = List.of(new Book(), new Book());
        when(bookRepository.findByTitle("Title")).thenReturn(books);

        List<Book> result = bookService.findBooksByTitle("Title");
        assertEquals(books, result);
    }

    @Test
    void testFindBooksByPublicationYear() {
        List<Book> books = List.of(new Book(), new Book());
        when(bookRepository.findByPublicationYear(2020)).thenReturn(books);

        List<Book> result = bookService.findBooksByPublicationYear(2020);
        assertEquals(books, result);
    }

    @Test
    void testSearchBooks() {
        List<Book> books = List.of(new Book(), new Book());
        when(bookRepository.searchBooks("keyword")).thenReturn(books);

        List<Book> result = bookService.searchBooks("keyword");
        assertEquals(books, result);
    }

    @Test
    void testFindBooksByAuthor() {
        List<Book> books = List.of(new Book(), new Book());
        when(bookRepository.findByAuthor(1)).thenReturn(books);

        List<Book> result = bookService.findBooksByAuthor(1);
        assertEquals(books, result);
    }

    @Test
    void testFindBooksByGenre() {
        List<Book> books = List.of(new Book(), new Book());
        when(bookRepository.findByGenre(1)).thenReturn(books);

        List<Book> result = bookService.findBooksByGenre(1);
        assertEquals(books, result);
    }

    @Test
    void testFindBooksByPublisher() {
        List<Book> books = List.of(new Book(), new Book());
        when(bookRepository.findByPublisher(1)).thenReturn(books);

        List<Book> result = bookService.findBooksByPublisher(1);
        assertEquals(books, result);
    }

    @Test
    void testFindPaginated() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Book> page = new PageImpl<>(List.of(new Book(), new Book()));
        when(bookRepository.findAll(pageable)).thenReturn(page);

        //Page<Book> result = bookService.findPaginated(pageable);
        //assertEquals(page, result);
    }

    @Test
    @SuppressWarnings("unchecked")
    void testFindFilteredAndPaginated() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Book> page = new PageImpl<>(List.of(new Book(), new Book()));
        Specification.where(null);
        when(bookRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);

        //Page<Book> result = bookService.findFilteredAndPaginated("Title", "Author", "Genre", "Publisher", 2020, pageable);
        //assertEquals(page, result);
    }

    @Test
    void testFindBookById() {
        Book book = new Book();
        when(bookRepository.findById(1)).thenReturn(Optional.of(book));

        Book result = bookService.findBookById(1);
        assertEquals(book, result);
    }

    @Test
    void testFindBookByIdNotFound() {
        when(bookRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bookService.findBookById(1));
    }

    @Test
    void testFindBookByIsbn() {
        Book book = new Book();
        when(bookRepository.findByIsbn("1234567890")).thenReturn(Optional.of(book));

        Book result = bookService.findBookByIsbn("1234567890");
        assertEquals(book, result);
    }

    @Test
    void testFindBookByIsbnNotFound() {
        when(bookRepository.findByIsbn("1234567890")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bookService.findBookByIsbn("1234567890"));
    }

    @Test
    void testAddBook() {
        Book book = new Book();
        when(bookRepository.findByIsbn(book.getIsbn())).thenReturn(Optional.empty());
        when(bookRepository.findById(book.getId())).thenReturn(Optional.empty());

        bookService.addBook(book);
        verify(bookRepository, times(1)).save(book);
    }

    @Test
    void testAddBookDuplicateIsbn() {
        Book book = new Book();
        when(bookRepository.findByIsbn(book.getIsbn())).thenReturn(Optional.of(book));

        assertThrows(DuplicateResourceException.class, () -> bookService.addBook(book));
    }

    @Test
    void testAddBookDuplicateId() {
        Book book = new Book();
        when(bookRepository.findByIsbn(book.getIsbn())).thenReturn(Optional.empty());
        when(bookRepository.findById(book.getId())).thenReturn(Optional.of(book));

        assertThrows(DuplicateResourceException.class, () -> bookService.addBook(book));
    }

    @Test
    void testUpdateBook() {
        Book book = new Book();

        bookService.updateBook(book);
        verify(bookRepository, times(1)).save(book);
    }

    @Test
    void testDeleteBook() {
        Book book = new Book();
        when(bookRepository.findById(1)).thenReturn(Optional.of(book));

        bookService.deleteBook(1);
        verify(bookRepository, times(1)).delete(book);
    }

    @Test
    void testDeleteBookNotFound() {
        when(bookRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bookService.deleteBook(1));
    }
}