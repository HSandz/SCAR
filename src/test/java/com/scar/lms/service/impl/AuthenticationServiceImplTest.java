package com.scar.lms.service.impl;

import com.scar.lms.entity.Role;
import com.scar.lms.entity.User;
import com.scar.lms.exception.InvalidDataException;
import com.scar.lms.exception.ResourceNotFoundException;
import com.scar.lms.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthenticationServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @InjectMocks
    private AuthenticationServiceImpl authenticationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testExtractUsernameFromOAuth2Authentication() {
        OAuth2AuthenticationToken token = mock(OAuth2AuthenticationToken.class);
        OAuth2User oAuth2User = mock(OAuth2User.class);

        when(token.getPrincipal()).thenReturn(oAuth2User);
        Map<String, Object> attributes = Map.of("login", "testuser");
        when(oAuth2User.getAttributes()).thenReturn(attributes);

        String username = authenticationService.extractUsernameFromAuthentication(token);
        assertEquals("testuser", username);
    }


    @Test
    void testExtractUsernameFromUsernamePasswordAuthentication() {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken("testuser", "password");

        String username = authenticationService.extractUsernameFromAuthentication(token);
        assertEquals("testuser", username);
    }

    @Test
    void testExtractUsernameFromOAuth2AuthenticationNullPrincipal() {
        OAuth2AuthenticationToken token = mock(OAuth2AuthenticationToken.class);
        when(token.getPrincipal()).thenReturn(null);

        assertThrows(InvalidDataException.class, () -> authenticationService.extractUsernameFromAuthentication(token));
    }

    @Test
    void testExtractUsernameFromOAuth2AuthenticationNullAttributes() {
        OAuth2AuthenticationToken token = mock(OAuth2AuthenticationToken.class);
        OAuth2User oAuth2User = mock(OAuth2User.class);
        when(token.getPrincipal()).thenReturn(oAuth2User);
        when(oAuth2User.getAttributes()).thenReturn(null);

        assertThrows(InvalidDataException.class, () -> authenticationService.extractUsernameFromAuthentication(token));
    }

    @Test
    void testExtractUsernameFromUnsupportedAuthentication() {
        Authentication unsupportedAuth = mock(Authentication.class);
        assertThrows(InvalidDataException.class, () -> authenticationService.extractUsernameFromAuthentication(unsupportedAuth));
    }

    @Test
    void testLoadUserByUsername() {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("password");
        user.setRole(Role.USER);
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        UserDetails userDetails = authenticationService.loadUserByUsername("testuser");
        assertEquals("testuser", userDetails.getUsername());
        assertEquals("password", userDetails.getPassword());
    }

    @Test
    void testLoadUserByUsernameNotFound() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> authenticationService.loadUserByUsername("testuser"));
    }

    @Test
    void testValidateRegistration() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

        boolean isValid = authenticationService.validateRegistration("testuser", "password123", "Test User", "test@example.com");
        assertTrue(isValid);
    }

    @Test
    void testUpdatePassword() {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("oldPasswordHash");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(bCryptPasswordEncoder.matches("oldPassword", "oldPasswordHash")).thenReturn(true);
        when(bCryptPasswordEncoder.encode("newPassword")).thenReturn("newPasswordHash");

        boolean isUpdated = authenticationService.updatePassword("testuser", "oldPassword", "newPassword");
        assertTrue(isUpdated);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testUpdatePasswordInvalidNewPassword() {
        boolean isUpdated = authenticationService.updatePassword("testuser", "oldPassword", "short");
        assertFalse(isUpdated);
    }

    @Test
    void testUpdatePasswordOldPasswordMismatch() {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("oldPasswordHash");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(bCryptPasswordEncoder.matches("oldPassword", "oldPasswordHash")).thenReturn(false);

        boolean isUpdated = authenticationService.updatePassword("testuser", "oldPassword", "newPassword");
        assertFalse(isUpdated);
    }
}