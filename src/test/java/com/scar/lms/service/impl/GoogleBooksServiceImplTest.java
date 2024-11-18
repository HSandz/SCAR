package com.scar.lms.service.impl;

import com.scar.lms.config.GoogleBooksApiProperties;
import com.scar.lms.entity.Book;
import com.scar.lms.exception.DuplicateResourceException;
import com.scar.lms.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GoogleBooksServiceImplTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private GoogleBooksApiProperties googleBooksApiProperties;

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private GoogleBooksServiceImpl googleBooksService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSearchBooks() throws Exception {
        String query = "test";
        int startIndex = 0;
        int maxResults = 10;
        String url = "https://www.googleapis.com/books/v1/volumes?q=test&startIndex=0&maxResults=10&key=API_KEY";

        when(googleBooksApiProperties.getUrl()).thenReturn("https://www.googleapis.com/books/v1/volumes");
        when(googleBooksApiProperties.getKey()).thenReturn("API_KEY");

        String jsonResponse = "{ \"items\": [ { \"volumeInfo\": { \"title\": \"Test Book\", \"industryIdentifiers\": [ { \"type\": \"ISBN_13\", \"identifier\": \"1234567890123\" } ], \"language\": \"en\", \"averageRating\": 4.5, \"publishedDate\": \"2020-01-01\", \"description\": \"Test Description\", \"authors\": [ \"Test Author\" ], \"imageLinks\": { \"thumbnail\": \"https://example.com/thumbnail.jpg\" }, \"publisher\": \"Test Publisher\", \"categories\": [ \"Test Category\" ] } } ] }";
        ResponseEntity<String> responseEntity = ResponseEntity.ok(jsonResponse);

        when(restTemplate.exchange(url, HttpMethod.GET, null, String.class)).thenReturn(responseEntity);

        List<Book> books = googleBooksService.searchBooks(query, startIndex, maxResults);

        assertNotNull(books);
        assertEquals(1, books.size());
        assertEquals("Test Book", books.getFirst().getTitle());
        assertEquals("1234567890123", books.getFirst().getIsbn());
        assertEquals("en", books.getFirst().getLanguage());
        assertEquals(4.5, books.getFirst().getRating());
        assertEquals(2020, books.getFirst().getPublicationYear());
        assertEquals("Test Description", books.getFirst().getDescription());
        assertEquals("Test Author", books.getFirst().getAuthors().iterator().next().getName());
        assertEquals("https://example.com/thumbnail.jpg", books.getFirst().getImageUrl());
        assertEquals("Test Publisher", books.getFirst().getPublishers().iterator().next().getName());
        assertEquals("Test Category", books.getFirst().getGenres().iterator().next().getName());
    }

    @Test
    void testSaveBook() {
        Book book = new Book();
        book.setIsbn("1234567890123");

        when(bookRepository.findByIsbn("1234567890123")).thenReturn(Optional.empty());

        googleBooksService.save(book);

        verify(bookRepository, times(1)).save(book);
    }

    @Test
    void testSaveBookDuplicate() {
        Book book = new Book();
        book.setIsbn("1234567890123");

        when(bookRepository.findByIsbn("1234567890123")).thenReturn(Optional.of(book));

        assertThrows(DuplicateResourceException.class, () -> googleBooksService.save(book));
    }
}