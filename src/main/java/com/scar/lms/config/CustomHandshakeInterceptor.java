package com.scar.lms.config;

import com.scar.lms.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;

import jakarta.servlet.http.HttpSession;
import java.util.Map;

@Component
public class CustomHandshakeInterceptor implements HandshakeInterceptor {

    private final UserService userService;

    public CustomHandshakeInterceptor(UserService userService) {
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
                Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                if (principal instanceof UserDetails) {
                    String username = ((UserDetails) principal).getUsername();
                    attributes.put("username", username);
                    attributes.put("profilePictureUrl", userService.findUsersByUsername(username).getProfilePictureUrl());
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