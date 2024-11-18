package com.scar.lms.config;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(properties = {
        "google.books.api.key=test-key",
        "google.books.api.url=http://test-url"
})
public class GoogleBooksApiPropertiesTest {

    @Autowired
    private GoogleBooksApiProperties googleBooksApiProperties;

    @Test
    public void propertiesShouldBeBound() {
        assertNotNull(googleBooksApiProperties);
        assertEquals("test-key", googleBooksApiProperties.getKey());
        assertEquals("http://test-url", googleBooksApiProperties.getUrl());
    }
}