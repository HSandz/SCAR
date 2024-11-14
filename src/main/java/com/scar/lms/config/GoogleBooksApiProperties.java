package com.scar.lms.config;

import lombok.Data;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "google.books.api")
@Data
public class GoogleBooksApiProperties {

    private String key;
    private String url;
}
