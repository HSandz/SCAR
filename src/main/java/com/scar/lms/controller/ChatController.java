package com.scar.lms.controller;

import com.scar.lms.model.ChatMessageDTO;
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
import java.util.concurrent.CompletableFuture;

@SuppressWarnings("SameReturnValue")
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
        try {
            String username = authenticationService.extractUsernameFromAuthentication(authentication).join();
            model.addAttribute("username", username);
            model.addAttribute("profilePictureUrl",
                    userService.findUserByUsername(username).join().getProfilePictureUrl());
        } catch (Exception e) {
            model.addAttribute("username", "Anonymous");
            model.addAttribute("profilePictureUrl", "https://i.imgur.com/8bYkY7j.png");
        }
        return "chat";
    }

    @PostMapping("")
    public String sendMessage(@RequestParam("userMessage") String userMessage, Model model) {
        model.addAttribute("userMessage", userMessage);
        return "chat";
    }

    @MessageMapping("/chat.addUser")
    @SendTo("/topic/chat")
    public ChatMessageDTO addUser(@Payload ChatMessageDTO chatMessageDTO, SimpMessageHeaderAccessor headerAccessor) {
        String username = (String) Objects.requireNonNull(headerAccessor.getSessionAttributes()).get("username");
        String profilePictureUrl = (String) headerAccessor.getSessionAttributes().get("profilePictureUrl");
        chatMessageDTO.setSender(username != null ? username : "Anonymous");
        chatMessageDTO.setType(ChatMessageDTO.MessageType.JOIN);
        chatMessageDTO.setContent((username != null ? username : "Anonymous") + " joined the chat");
        chatMessageDTO.setProfilePictureUrl(profilePictureUrl);
        return chatMessageDTO;
    }

    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/chat")
    public ChatMessageDTO sendMessage(@Payload ChatMessageDTO chatMessageDTO, SimpMessageHeaderAccessor headerAccessor) {
        String username = (String) Objects.requireNonNull(headerAccessor.getSessionAttributes()).get("username");
        String profilePictureUrl = (String) headerAccessor.getSessionAttributes().get("profilePictureUrl");
        chatMessageDTO.setSender(username != null ? username : "Anonymous");
        chatMessageDTO.setProfilePictureUrl(profilePictureUrl);
        return chatMessageDTO;
    }

    @GetMapping("/bot")
    public String showChatBotPage() {
        return "chat-bot";
    }

    @PostMapping("/bot")
    public String sendMessageBot(@RequestParam("userMessage") String userMessage, Model model) {
        CompletableFuture<String> botResponseFuture = openAIService.getResponse(userMessage);

        botResponseFuture.thenAccept(botResponse -> {
            model.addAttribute("userMessage", userMessage);
            model.addAttribute("botResponse", botResponse);
        }).exceptionally(_ -> {
            model.addAttribute("userMessage", userMessage);
            model.addAttribute("botResponse", "Sorry, something went wrong while processing your request.");
            return null;
        });

        return "chat-bot";
    }
}