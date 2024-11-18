package com.scar.lms.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OpenAIServiceImplTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private OpenAIServiceImpl openAIService;

    @Value("${openai.api.key}")
    private String apiKey;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetResponse() throws Exception {
        String userMessage = "Hello, OpenAI!";
        String responseBody = "{\"choices\": [{\"message\": {\"content\": \"Hello, user!\"}}]}";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiKey);
        headers.set("Content-Type", "application/json");

        String requestBody = String.format(
                "{\"model\": \"gpt-3.5-turbo\", \"messages\": [{\"role\": \"user\", \"content\": \"%s\"}], \"max_tokens\": 100}",
                userMessage
        );

        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> responseEntity = new ResponseEntity<>(responseBody, HttpStatus.OK);
        when(restTemplate.exchange(
                eq("https://api.openai.com/v1/chat/completions"),
                eq(HttpMethod.POST),
                eq(entity),
                eq(String.class)
        )).thenReturn(responseEntity);

        String response = openAIService.getResponse(userMessage);
        assertEquals("Hello, user!", response);
    }

    @Test
    void testGetResponseError() throws Exception {
        String userMessage = "Hello, OpenAI!";
        String responseBody = "{\"error\": \"Invalid request\"}";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiKey);
        headers.set("Content-Type", "application/json");

        String requestBody = String.format(
                "{\"model\": \"gpt-3.5-turbo\", \"messages\": [{\"role\": \"user\", \"content\": \"%s\"}], \"max_tokens\": 100}",
                userMessage
        );

        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> responseEntity = new ResponseEntity<>(responseBody, HttpStatus.BAD_REQUEST);
        when(restTemplate.exchange(
                eq("https://api.openai.com/v1/chat/completions"),
                eq(HttpMethod.POST),
                eq(entity),
                eq(String.class)
        )).thenReturn(responseEntity);

        String response = openAIService.getResponse(userMessage);
        assertEquals("Error processing OpenAI response", response);
    }
}