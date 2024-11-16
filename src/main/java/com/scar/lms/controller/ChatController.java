package com.scar.lms.controller;

import com.scar.lms.service.OpenAIService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/chat")
public class ChatController {

    private final OpenAIService openAIService;

    public ChatController(final OpenAIService openAIService) {
        this.openAIService = openAIService;
    }

    @GetMapping("/chat")
    public String showChatPage() {
        return "chat";
    }

    @PostMapping("/chat")
    public String sendMessage(@RequestParam("userMessage") String userMessage, Model model) {
        String botResponse = openAIService.getResponse(userMessage);

        model.addAttribute("userMessage", userMessage);
        model.addAttribute("botResponse", botResponse);

        return "chat";
    }
}
