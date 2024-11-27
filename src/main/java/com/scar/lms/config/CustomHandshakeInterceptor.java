package com.scar.lms.config;

import com.scar.lms.exception.InvalidDataException;
import com.scar.lms.service.AuthenticationService;
import com.scar.lms.service.UserService;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;

import jakarta.servlet.http.HttpSession;

import java.util.Map;

@Slf4j
@Component
public class CustomHandshakeInterceptor implements HandshakeInterceptor {

    private final UserService userService;
    private final AuthenticationService authenticationService;

    public CustomHandshakeInterceptor(final UserService userService,
                                      final AuthenticationService authenticationService) {
        this.userService = userService;
        this.authenticationService = authenticationService;
    }

    @Override
    public boolean beforeHandshake(@NonNull ServerHttpRequest request,
                                   @NonNull ServerHttpResponse response,
                                   @NonNull WebSocketHandler wsHandler,
                                   @NonNull Map<String, Object> attributes) {
        if (request instanceof org.springframework.http.server.ServletServerHttpRequest servletRequest) {
            HttpSession session = servletRequest.getServletRequest().getSession(false);
            if (session != null) {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                if (authentication instanceof UsernamePasswordAuthenticationToken) {

                    System.out.println("UsernamePassword detected");
                    String username = authentication.getName();
                    attributes.put("username", username);
                    String profilePictureUrl = userService.findUserByUsername(username).join().getProfilePictureUrl();
                    if (profilePictureUrl != null) {
                        attributes.put("profilePictureUrl", profilePictureUrl);
                    }
                } else if (authentication instanceof OAuth2AuthenticationToken token) {
                    if (token.getPrincipal() == null || token.getPrincipal().getAttributes() == null) {
                        throw new InvalidDataException("OAuth2 token principal or attributes are null");
                    }
                    System.out.println("OAuth2 detected");
                    Map<String, Object> attribute = token.getPrincipal().getAttributes();
                    String username = authenticationService.extractUsernameFromAuthentication(authentication).join();
                    attributes.put("username", username);
                    if (attribute.get("avatar_url") != null) {
                        attributes.put("profilePictureUrl", attribute.get("avatar_url"));
                    }
                } else {
                    attributes.put("username", "Anonymous");
                    attributes.put("profilePictureUrl", null);
                }
            }
        }
        return true;
    }

    @Override
    public void afterHandshake(@NonNull ServerHttpRequest request,
                               @NonNull ServerHttpResponse response,
                               @NonNull WebSocketHandler wsHandler,
                               Exception ex) {
    }
}