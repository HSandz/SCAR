package com.scar.lms.service.impl;

import com.scar.lms.entity.User;
import com.scar.lms.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Map;
import java.util.Optional;

import static com.scar.lms.entity.Role.USER;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GitHubOAuth2ServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Mock
    private OAuth2User oAuth2User;

    @InjectMocks
    private GitHubOAuth2ServiceImpl gitHubOAuth2Service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegisterNewUser_NewUser() {
        Map<String, Object> attributes = Map.of(
                "login", "testuser",
                "id", 12345,
                "name", "Test User"
        );

        when(oAuth2User.getAttributes()).thenReturn(attributes);
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());
        when(bCryptPasswordEncoder.encode(anyString())).thenReturn("encodedPassword");

        User newUser = new User();
        newUser.setUsername("testuser");
        newUser.setDisplayName("Test User");
        newUser.setEmail("githubtestuser@gmail.com");
        newUser.setRole(USER);
        newUser.setPassword("encodedPassword");

        when(userRepository.save(any(User.class))).thenReturn(newUser);

        User result = gitHubOAuth2Service.registerNewUser(oAuth2User);

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals("Test User", result.getDisplayName());
        assertEquals("githubtestuser@gmail.com", result.getEmail());
        assertEquals(USER, result.getRole());
        assertEquals("encodedPassword", result.getPassword());

        verify(userRepository, times(1)).findByUsername("testuser");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testRegisterNewUser_ExistingUser() {
        Map<String, Object> attributes = Map.of(
                "login", "testuser",
                "id", 12345,
                "name", "Test User"
        );

        when(oAuth2User.getAttributes()).thenReturn(attributes);

        User existingUser = new User();
        existingUser.setUsername("testuser");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(existingUser));

        User result = gitHubOAuth2Service.registerNewUser(oAuth2User);

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());

        verify(userRepository, times(1)).findByUsername("testuser");
        verify(userRepository, never()).save(any(User.class));
    }
}