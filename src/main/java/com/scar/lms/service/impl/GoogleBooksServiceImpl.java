package com.scar.lms.service.impl;

import ch.qos.logback.classic.Logger;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.scar.lms.config.GoogleBooksApiProperties;
import com.scar.lms.entity.Author;
import com.scar.lms.entity.Book;
import com.scar.lms.entity.Genre;
import com.scar.lms.entity.Publisher;
import com.scar.lms.exception.DuplicateResourceException;
import com.scar.lms.repository.BookRepository;
import com.scar.lms.service.GoogleBooksService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class GoogleBooksServiceImpl implements GoogleBooksService {

    private final RestTemplate restTemplate;
    private final GoogleBooksApiProperties googleBooksApiProperties;
    private final BookRepository bookRepository;

    public GoogleBooksServiceImpl(final RestTemplate restTemplate,
                                  final GoogleBooksApiProperties googleBooksApiProperties,
                                  final BookRepository bookRepository) {
        this.restTemplate = restTemplate;
        this.googleBooksApiProperties = googleBooksApiProperties;
        this.bookRepository = bookRepository;
    }

    @Override
    @Async
    public CompletableFuture<List<Book>> searchBooks(String query, int startIndex, int maxResults) {
        if (query == null || query.trim().isEmpty()) {
            return CompletableFuture.completedFuture(Collections.emptyList());
        }

        String url = googleBooksApiProperties.getUrl() + "?q=" + query
                + "&startIndex=" + startIndex
                + "&maxResults=" + maxResults
                + "&key=" + googleBooksApiProperties.getKey();

        try {
            // Call Google Books API
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, null, String.class);
            String jsonResponse = response.getBody();

            // Parse JSON response
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(jsonResponse);
            JsonNode itemsNode = rootNode.path("items");

            // Map JSON data to List<Book>
            List<Book> books = new ArrayList<>();
            for (JsonNode item : itemsNode) {
                Book book = new Book();

                // Title
                book.setTitle(item.path("volumeInfo").path("title").asText());

                // ISBN
                JsonNode isbnNode = item.path("volumeInfo").path("industryIdentifiers");
                if (isbnNode.isArray()) {
                    for (JsonNode identifier : isbnNode) {
                        if ("ISBN_13".equals(identifier.path("type").asText())) {
                            book.setIsbn(identifier.path("identifier").asText());
                            break;
                        }
                    }
                }

                // Language
                book.setLanguage(item.path("volumeInfo").path("language").asText());

                // Rating
                if (item.path("volumeInfo").has("averageRating")) {
                    book.setRating(item.path("volumeInfo").path("averageRating").asDouble());
                }

                // Publication Year
                String publishedDate = item.path("volumeInfo").path("publishedDate").asText();
                if (publishedDate.length() >= 4) {
                    book.setPublicationYear(Integer.parseInt(publishedDate.substring(0, 4)));
                }

                // Description
                book.setDescription(item.path("volumeInfo").path("description").asText());

                // Authors
                Set<Author> authors = new HashSet<>();
                JsonNode authorsNode = item.path("volumeInfo").path("authors");
                if (authorsNode.isArray()) {
                    for (JsonNode authorName : authorsNode) {
                        Author author = new Author();
                        author.setName(authorName.asText());
                        authors.add(author);
                    }
                }
                book.setAuthors(authors);

                // Image URL
                JsonNode imageLinksNode = item.path("volumeInfo").path("imageLinks");
                if (imageLinksNode.has("thumbnail")) {
                    book.setImageUrl(imageLinksNode.path("thumbnail").asText());
                }

                // Publisher
                Set<Publisher> publishers = new HashSet<>();
                String publisherName = item.path("volumeInfo").path("publisher").asText();
                if (!publisherName.isEmpty()) {
                    Publisher publisher = new Publisher();
                    publisher.setName(publisherName);
                    publishers.add(publisher);
                }
                book.setPublishers(publishers);

                // Genres (Categories)
                Set<Genre> genres = new HashSet<>();
                JsonNode categoriesNode = item.path("volumeInfo").path("categories");
                if (categoriesNode.isArray()) {
                    for (JsonNode category : categoriesNode) {
                        Genre genre = new Genre();
                        genre.setName(category.asText());
                        genres.add(genre);
                    }
                }
                book.setGenres(genres);

                // Main Genre
                if (categoriesNode.isArray() && !categoriesNode.isEmpty()) {
                    book.setMainGenre(categoriesNode.get(0).asText());
                }

                books.add(book);
            }

            // Return CompletableFuture
            return CompletableFuture.completedFuture(books);
        } catch (JsonProcessingException e) {
            log.error("Error parsing JSON response from Google Books API", e);
            return CompletableFuture.completedFuture(Collections.emptyList());
        } catch (Exception e) {
            log.error("Error calling Google Books API", e);
            return CompletableFuture.completedFuture(Collections.emptyList());
        }
    }
}
