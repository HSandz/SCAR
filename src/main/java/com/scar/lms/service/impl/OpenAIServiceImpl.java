package com.scar.lms.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.scar.lms.service.OpenAIService;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class OpenAIServiceImpl implements OpenAIService {

    @Value("${openai.api.key}")
    private String apiKey;

    private static final String API_URL = "https://api.openai.com/v1/chat/completions";

    @Async
    @Override
    public CompletableFuture<String> getResponse(String userMessage) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiKey);
        headers.set("Content-Type", "application/json");
        String requestBody = String.format(
                "{\"model\": \"gpt-3.5-turbo\", \"messages\": [{\"role\": \"user\", \"content\": \"%s\"}], \"max_tokens\": 100}",
                userMessage
        );

        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        try {
            return getChatResponseCompletableFuture(restTemplate, entity);
        } catch (Exception e) {
            log.error("Error processing OpenAI response", e);
            return CompletableFuture.completedFuture("Error processing OpenAI response");
        }
    }

    private CompletableFuture<String> getChatResponseCompletableFuture(RestTemplate restTemplate, HttpEntity<String> entity) throws JsonProcessingException {
        ResponseEntity<String> response = restTemplate.exchange(
                API_URL,
                HttpMethod.POST,
                entity,
                String.class
        );
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonResponse = mapper.readTree(response.getBody());
        String chatResponse = jsonResponse.get("choices").get(0).get("message").get("content").asText();
        return CompletableFuture.completedFuture(chatResponse);
    }
}
