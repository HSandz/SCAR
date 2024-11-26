package com.scar.lms.config;

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

    public CustomHandshakeInterceptor(final UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean beforeHandshake(ServerHttpRequest request,
                                   ServerHttpResponse response,
                                   WebSocketHandler wsHandler,
                                   Map<String, Object> attributes) {
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

                    System.out.println("OAuth2 detected");
                    Map<String, Object> attribute = token.getPrincipal().getAttributes();
                    String username = (String) attribute.get("login");
                    String profilePictureUrl = (String) attribute.get("avatar_url");
                    if (username != null) {
                        attributes.put("username", username);
                    }
                    if (profilePictureUrl != null) {
                        attributes.put("profilePictureUrl", profilePictureUrl);
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
    public void afterHandshake(ServerHttpRequest request,
                               ServerHttpResponse response,
                               WebSocketHandler wsHandler,
                               Exception ex) {
    }
}