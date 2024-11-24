package com.scar.lms.service;

import java.util.concurrent.CompletableFuture;

public interface OpenAIService {

    CompletableFuture<String> getResponse(String userMessage);
}
