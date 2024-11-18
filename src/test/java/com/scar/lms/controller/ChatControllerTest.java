package com.scar.lms.controller;

import com.scar.lms.service.OpenAIService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.springframework.ui.Model;
import org.springframework.validation.support.BindingAwareModelMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class ChatControllerTest {

    @Mock
    private OpenAIService openAIService;

    @InjectMocks
    private ChatController chatController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testShowChatPage() {
        String viewName = chatController.showChatPage();
        assertEquals("chat", viewName);
    }

    @Test
    public void testSendMessage() {
        String userMessage = "Hello";
        String botResponse = "Hi there!";

        when(openAIService.getResponse(anyString())).thenReturn(botResponse);

        Model model = new BindingAwareModelMap();
        String viewName = chatController.sendMessage(userMessage, model);

        assertEquals("chat", viewName);
        assertEquals(userMessage, model.getAttribute("userMessage"));
        assertEquals(botResponse, model.getAttribute("botResponse"));
    }
}