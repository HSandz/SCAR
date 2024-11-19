package com.scar.lms.controller;

import com.scar.lms.entity.User;
import com.scar.lms.model.ChatMessage;
import com.scar.lms.service.AuthenticationService;
import com.scar.lms.service.OpenAIService;
import com.scar.lms.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.security.core.Authentication;

import org.springframework.ui.Model;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ui.Model;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class ChatControllerTest {

    @Mock
    private AuthenticationService authenticationService;

    @Mock
    private OpenAIService openAIService;

    @Mock
    private UserService userService;

    @Mock
    private Model model;

    @Mock
    private Authentication authentication;

    @Mock
    private UserDetails userDetails;

    @Mock
    private SimpMessageHeaderAccessor headerAccessor;

    @InjectMocks
    private ChatController chatController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testShowChatPage_WithAuthenticatedUser() {
        when(authenticationService.extractUsernameFromAuthentication(authentication)).thenReturn("testUser");
        User user = new User();
        user.setUsername("testUser");
        user.setProfilePictureUrl("https://example.com/pic.jpg");
        when(userService.findUsersByUsername("testUser")).thenReturn(user);

        String view = chatController.showChatPage(authentication, model);

        verify(model).addAttribute("username", "testUser");
        verify(model).addAttribute("profilePictureUrl", "https://example.com/pic.jpg");
        assertEquals("chat", view);
    }

    @Test
    void testSendMessage() {
        String userMessage = "Hello!";
        String view = chatController.sendMessage(userMessage, model);

        verify(model).addAttribute("userMessage", userMessage);
        assertEquals("chat", view);
    }

    @Test
    void testAddUser() {
        ChatMessage chatMessage = new ChatMessage();
        when(headerAccessor.getSessionAttributes()).thenReturn(Map.of("username", "testUser", "profilePictureUrl", "https://example.com/pic.jpg"));

        ChatMessage result = chatController.addUser(chatMessage, headerAccessor);

        assertEquals("testUser", result.getSender());
        assertEquals(ChatMessage.MessageType.JOIN, result.getType());
        assertEquals("testUser joined the chat", result.getContent());
        assertEquals("https://example.com/pic.jpg", result.getProfilePictureUrl());
    }

    @Test
    void testSendMessage_WebSocket() {
        ChatMessage chatMessage = new ChatMessage();
        when(headerAccessor.getSessionAttributes()).thenReturn(Map.of("username", "testUser", "profilePictureUrl", "https://example.com/pic.jpg"));

        ChatMessage result = chatController.sendMessage(chatMessage, headerAccessor);

        assertEquals("testUser", result.getSender());
        assertEquals("https://example.com/pic.jpg", result.getProfilePictureUrl());
    }

    @Test
    void testShowChatBotPage() {
        String view = chatController.showChatBotPage();

        assertEquals("chat-bot", view);
    }

    @Test
    void testSendMessageBot() {
        String userMessage = "Hello, bot!";
        when(openAIService.getResponse(userMessage)).thenReturn("Hi, how can I help you?");

        String view = chatController.sendMessageBot(userMessage, model);

        verify(model).addAttribute("userMessage", userMessage);
        verify(model).addAttribute("botResponse", "Hi, how can I help you?");
        assertEquals("chat-bot", view);
    }
}