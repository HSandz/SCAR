package com.scar.lms.controller;

import com.scar.lms.service.OpenAIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ChatController {

    @Autowired
    private OpenAIService openAIService;

    @GetMapping("/chat")
    public String showChatPage() {
        return "chat";
    }

    @PostMapping("/chat")
    public String sendMessage(@RequestParam("userMessage") String userMessage, Model model) {
        // Send message to OpenAI API and get response
        String botResponse = openAIService.getResponse(userMessage);

        // Add user message and bot response to the model for display
        model.addAttribute("userMessage", userMessage);
        model.addAttribute("botResponse", botResponse);

        return "chat";
    }
}
