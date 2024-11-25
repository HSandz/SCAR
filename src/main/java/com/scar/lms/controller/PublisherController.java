package com.scar.lms.controller;

import com.scar.lms.entity.Publisher;
import com.scar.lms.service.PublisherService;

import jakarta.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/admin/publishers")
public class PublisherController {

    private final PublisherService publisherService;

    public PublisherController(PublisherService publisherService) {
        this.publisherService = publisherService;
    }

    @GetMapping({"/", ""})
    public String listAllPublisher(Model model) {
        List<Publisher> publishers = publisherService.findAllPublishers().join();
        model.addAttribute("publishers", publishers);
        return "publishers";
    }

    @GetMapping("/add")
    public String showAddPublisherForm(Model model) {
        model.addAttribute("publisher", new Publisher());
        return "add-publisher";
    }

    @PostMapping("/add")
    public String addPublisher(@Valid @ModelAttribute Publisher publisher, BindingResult result) {
        if (result.hasErrors()) {
            return "add-publisher";
        }
        publisherService.createPublisher(publisher);
        return "redirect:/publishers";
    }
}
