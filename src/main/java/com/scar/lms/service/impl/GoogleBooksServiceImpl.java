package com.scar.lms.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.scar.lms.config.GoogleBooksApiProperties;
import com.scar.lms.entity.Book;
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

    public GoogleBooksServiceImpl(final RestTemplate restTemplate,
                                  final GoogleBooksApiProperties googleBooksApiProperties) {
        this.restTemplate = restTemplate;
        this.googleBooksApiProperties = googleBooksApiProperties;
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
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, null, String.class);
            String jsonResponse = response.getBody();
            ObjectMapper objectMapper = new ObjectMapper();
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
                JsonNode authorsNode = item.path("volumeInfo").path("authors");
                if (authorsNode.isArray() && !authorsNode.isEmpty()) {
                    book.setAuthor(authorsNode.get(0).asText());
                }
                JsonNode imageLinksNode = item.path("volumeInfo").path("imageLinks");
                if (imageLinksNode.has("thumbnail")) {
                    book.setImageUrl(imageLinksNode.path("thumbnail").asText());
                }
                String publisherName = item.path("volumeInfo").path("publisher").asText();
                if (!publisherName.isEmpty()) {
                    book.setPublisher(publisherName);
                }
                JsonNode categoriesNode = item.path("volumeInfo").path("categories");
                if (categoriesNode.isArray() && categoriesNode.size() > 0) {
                    book.setGenre(categoriesNode.get(0).asText());
                }

                books.add(book);
            }
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