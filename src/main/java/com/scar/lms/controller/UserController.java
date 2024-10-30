package com.scar.lms.controller;

import com.scar.lms.service.UserService;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    private final UserService userService;

    public UserController(final UserService userService) {
        this.userService = userService;
    }
}
