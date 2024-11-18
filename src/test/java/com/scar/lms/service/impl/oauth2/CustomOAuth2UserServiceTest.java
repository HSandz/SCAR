package com.scar.lms.service.impl.oauth2;

import com.scar.lms.exception.OperationNotAllowedException;
import com.scar.lms.service.GitHubOAuth2Service;
import com.scar.lms.service.GoogleOAuth2Service;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;

import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CustomOAuth2UserServiceTest {

    @Mock
    private GoogleOAuth2Service googleOAuth2Service;

    @Mock
    private GitHubOAuth2Service githubOAuth2Service;

    @Mock
    private DefaultOAuth2UserService defaultOAuth2UserService;

    @InjectMocks
    private CustomOAuth2UserService customOAuth2UserService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testLoadUser_Google() {
        // Mock UserRequest and ClientRegistration
        OAuth2UserRequest userRequest = mock(OAuth2UserRequest.class);
        ClientRegistration clientRegistration = mock(ClientRegistration.class);
        ClientRegistration.ProviderDetails providerDetails = mock(ClientRegistration.ProviderDetails.class);
        ClientRegistration.ProviderDetails.UserInfoEndpoint userInfoEndpoint = mock(ClientRegistration.ProviderDetails.UserInfoEndpoint.class);
        OAuth2AccessToken accessToken = mock(OAuth2AccessToken.class);

        // Configure the mock ClientRegistration
        when(userRequest.getClientRegistration()).thenReturn(clientRegistration);
        when(clientRegistration.getRegistrationId()).thenReturn("google");
        when(clientRegistration.getProviderDetails()).thenReturn(providerDetails);
        when(providerDetails.getUserInfoEndpoint()).thenReturn(userInfoEndpoint);
        when(userInfoEndpoint.getUri()).thenReturn("https://www.googleapis.com/oauth2/v3/userinfo");
        when(userRequest.getAccessToken()).thenReturn(accessToken);
        when(accessToken.getTokenValue()).thenReturn("dummy-token");

        // Mock the user returned by the DefaultOAuth2UserService
        OAuth2User oAuth2User = new DefaultOAuth2User(
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")),
                Map.of("sub", "12345"),
                "sub"
        );
        when(defaultOAuth2UserService.loadUser(userRequest)).thenReturn(oAuth2User);

        // Call the method and verify behavior
        OAuth2User result = customOAuth2UserService.loadUser(userRequest);

        verify(googleOAuth2Service).registerNewUser(oAuth2User);
        assertEquals("12345", result.getName());
    }

    @Test
    void testLoadUser_GitHub() {
        // Mock UserRequest and ClientRegistration
        OAuth2UserRequest userRequest = mock(OAuth2UserRequest.class);
        ClientRegistration clientRegistration = mock(ClientRegistration.class);
        ClientRegistration.ProviderDetails providerDetails = mock(ClientRegistration.ProviderDetails.class);
        ClientRegistration.ProviderDetails.UserInfoEndpoint userInfoEndpoint = mock(ClientRegistration.ProviderDetails.UserInfoEndpoint.class);
        OAuth2AccessToken accessToken = mock(OAuth2AccessToken.class);

        // Configure the mock ClientRegistration
        when(userRequest.getClientRegistration()).thenReturn(clientRegistration);
        when(clientRegistration.getRegistrationId()).thenReturn("github");
        when(clientRegistration.getProviderDetails()).thenReturn(providerDetails);
        when(providerDetails.getUserInfoEndpoint()).thenReturn(userInfoEndpoint);
        when(userInfoEndpoint.getUri()).thenReturn("https://api.github.com/user");
        when(userRequest.getAccessToken()).thenReturn(accessToken);
        when(accessToken.getTokenValue()).thenReturn("dummy-token");

        // Mock the user returned by the DefaultOAuth2UserService
        OAuth2User mockedOAuth2User = new DefaultOAuth2User(
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")),
                Map.of("login", "testuser", "id", "67890"),
                "login"
        );
        when(defaultOAuth2UserService.loadUser(userRequest)).thenReturn(mockedOAuth2User);

        // Call the method and verify behavior
        OAuth2User result = customOAuth2UserService.loadUser(userRequest);

        verify(githubOAuth2Service).registerNewUser(mockedOAuth2User);
        assertEquals("testuser", result.getName());
    }


    @Test
    void testLoadUser_UnsupportedProvider() {
        OAuth2UserRequest userRequest = mock(OAuth2UserRequest.class);
        ClientRegistration clientRegistration = mock(ClientRegistration.class);
        when(userRequest.getClientRegistration()).thenReturn(clientRegistration);
        when(clientRegistration.getRegistrationId()).thenReturn("unsupported");

        assertThrows(OperationNotAllowedException.class, () -> customOAuth2UserService.loadUser(userRequest));
    }

}