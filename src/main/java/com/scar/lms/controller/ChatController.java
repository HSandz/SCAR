package com.scar.lms.controller;

import com.scar.lms.model.ChatMessage;
import com.scar.lms.service.AuthenticationService;
import com.scar.lms.service.OpenAIService;
import com.scar.lms.service.UserService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Objects;

@Controller
@RequestMapping("/chat")
public class ChatController {

    private final AuthenticationService authenticationService;
    private final OpenAIService openAIService;
    private final UserService userService;

    public ChatController(final AuthenticationService authenticationService,
                          final OpenAIService openAIService,
                          final UserService userService) {
        this.authenticationService = authenticationService;
        this.openAIService = openAIService;
        this.userService = userService;
    }

    @GetMapping("")
    public String showChatPage(Authentication authentication, Model model) {
        String username = authenticationService.extractUsernameFromAuthentication(authentication);
        model.addAttribute("username", username);
        model.addAttribute("profilePictureUrl", userService.findUsersByUsername(username).getProfilePictureUrl());
        return "chat";
    }

    @PostMapping("")
    public String sendMessage(@RequestParam("userMessage") String userMessage, Model model) {
        model.addAttribute("userMessage", userMessage);
        return "chat";
    }

    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public")
    public ChatMessage addUser(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {
        String username = (String) Objects.requireNonNull(headerAccessor.getSessionAttributes()).get("username");
        String profilePictureUrl = (String) headerAccessor.getSessionAttributes().get("profilePictureUrl");
        chatMessage.setSender(username != null ? username : "Anonymous");
        chatMessage.setType(ChatMessage.MessageType.JOIN);
        chatMessage.setContent((username != null ? username : "Anonymous") + " joined the chat");
        chatMessage.setProfilePictureUrl(profilePictureUrl);
        return chatMessage;
    }

    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/chat")
    public ChatMessage sendMessage(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {
        String username = (String) Objects.requireNonNull(headerAccessor.getSessionAttributes()).get("username");
        String profilePictureUrl = (String) headerAccessor.getSessionAttributes().get("profilePictureUrl");
        chatMessage.setSender(username != null ? username : "Anonymous");
        chatMessage.setProfilePictureUrl(profilePictureUrl);
        return chatMessage;
    }

    @GetMapping("/bot")
    public String showChatBotPage() {
        return "chat-bot";
    }

    @PostMapping("/bot")
    public String sendMessageBot(@RequestParam("userMessage") String userMessage, Model model) {
        String botResponse = openAIService.getResponse(userMessage);
        model.addAttribute("userMessage", userMessage);
        model.addAttribute("botResponse", botResponse);
        return "chat-bot";
    }
}