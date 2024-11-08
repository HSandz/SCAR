package com.scar.lms.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scar.lms.config.GoogleBooksApiProperties;
import com.scar.lms.entity.Author;
import com.scar.lms.entity.Book;
import com.scar.lms.service.GoogleBooksService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class GoogleBooksServiceImpl implements GoogleBooksService {

    private final RestTemplate restTemplate;
    private final GoogleBooksApiProperties googleBooksApiProperties;

    @Autowired
    public GoogleBooksServiceImpl(RestTemplate restTemplate, GoogleBooksApiProperties googleBooksApiProperties) {
        this.restTemplate = restTemplate;
        this.googleBooksApiProperties = googleBooksApiProperties;
    }

    public List<Book> searchBooks(String query, int startIndex, int maxResults) {
        if (query == null || query.trim().isEmpty()) {
            return Collections.emptyList();
        }
        // Construct the URL with pagination parameters
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

                book.setTitle(item.path("volumeInfo").path("title").asText());

                JsonNode isbnNode = item.path("volumeInfo").path("industryIdentifiers");
                if (isbnNode.isArray()) {
                    for (JsonNode identifier : isbnNode) {
                        if ("ISBN_13".equals(identifier.path("type").asText())) {
                            book.setIsbn(identifier.path("identifier").asText());
                            break;
                        }
                    }
                }

                book.setLanguage(item.path("volumeInfo").path("language").asText());

                if (item.path("volumeInfo").has("averageRating")) {
                    book.setRating(item.path("volumeInfo").path("averageRating").asDouble());
                }

                String publishedDate = item.path("volumeInfo").path("publishedDate").asText();
                if (publishedDate.length() >= 4) {
                    book.setPublicationYear(Integer.parseInt(publishedDate.substring(0, 4)));
                }

                book.setDescription(item.path("volumeInfo").path("description").asText());

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

                books.add(book);
            }
            return books;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}
