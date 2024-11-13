package com.scar.lms.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scar.lms.config.GoogleBooksApiProperties;
import com.scar.lms.entity.Author;
import com.scar.lms.entity.Book;
import com.scar.lms.entity.Genre;
import com.scar.lms.entity.Publisher;
import com.scar.lms.repository.BookRepository;
import com.scar.lms.service.GoogleBooksService;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

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
    public List<Book> searchBooks(String query, int startIndex, int maxResults) {
        if (query == null || query.trim().isEmpty()) {
            return Collections.emptyList();
        }

        String url = googleBooksApiProperties.getUrl() + "?q=" + query
                + "&startIndex=" + startIndex
                + "&maxResults=" + maxResults
                + "&key=" + googleBooksApiProperties.getKey();

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, null, String.class);
        String jsonResponse = response.getBody();

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode rootNode = objectMapper.readTree(jsonResponse);
            JsonNode itemsNode = rootNode.path("items");

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

                books.add(book);
            }
            return books;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    @Override
    public void save(Book book) {
        if (bookRepository.findByIsbn(book.getIsbn()).isEmpty()) {
            bookRepository.save(book);
        }
    }
}
