package com.scar.lms.controller;

import com.scar.lms.entity.User;
import com.scar.lms.service.AuthenticationService;
import com.scar.lms.service.UserService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.springframework.ui.Model;
import org.springframework.validation.support.BindingAwareModelMap;

import static com.scar.lms.controller.IndexController.DEFAULT_USER_POINT;
import static com.scar.lms.entity.Role.USER;

import static org.junit.jupiter.api.Assertions.assertEquals;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class IndexControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private AuthenticationService authenticationService;

    @InjectMocks
    private IndexController indexController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testDefaultHome() {
        String viewName = indexController.defaultHome();
        assertEquals("redirect:/home", viewName);
    }

    @Test
    public void testShowRegisterForm() {
        Model model = new BindingAwareModelMap();
        String viewName = indexController.showRegisterForm(model);

        System.out.println("View Name: " + viewName);
        System.out.println("Model: " + model);

        assertEquals("register", viewName);
        assertEquals(new User(), model.getAttribute("user"));
    }

    @Test
    public void testRegisterUser_Success() {
        User user = new User();
        user.setUsername("testUser");
        user.setPassword("testPass");
        user.setDisplayName("Test User");
        user.setEmail("test@example.com");

        when(authenticationService.validateRegistration(any(), any(), any(), any())).thenReturn(true);
        when(authenticationService.encryptPassword(any())).thenReturn("encryptedPass");

        Model model = new BindingAwareModelMap();
        String viewName = indexController.registerUser(user, model);

        assertEquals("redirect:/login", viewName);
        assertEquals("encryptedPass", user.getPassword());
        assertEquals(DEFAULT_USER_POINT, user.getPoints());
        assertEquals(USER, user.getRole());
        verify(userService, times(1)).createUser(user);
    }

    @Test
    public void testRegisterUser_Failure() {
        User user = new User();
        user.setUsername("testUser");
        user.setPassword("testPass");
        user.setDisplayName("Test User");
        user.setEmail("test@example.com");

        when(authenticationService.validateRegistration(any(), any(), any(), any())).thenReturn(false);

        Model model = new BindingAwareModelMap();
        String viewName = indexController.registerUser(user, model);

        assertEquals("register", viewName);
        assertEquals("Invalid registration details.", model.getAttribute("error"));
        verify(userService, never()).createUser(any());
    }

    @Test
    public void testShowLoginPage() {
        String viewName = indexController.showLoginPage();
        assertEquals("login", viewName);
    }

    @Test
    public void testShowHomePage() {
        String viewName = indexController.showHomePage();
        assertEquals("home", viewName);
    }
}