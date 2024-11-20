package com.scar.lms.config;

import com.scar.lms.service.AuthenticationService;
import com.scar.lms.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
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
    private final AuthenticationService authenticationService;

    public CustomHandshakeInterceptor(final UserService userService,
                                      final AuthenticationService authenticationService) {
        this.userService = userService;
        this.authenticationService = authenticationService;
    }

    @Override
    public boolean beforeHandshake(ServerHttpRequest request,
                                   ServerHttpResponse response,
                                   WebSocketHandler wsHandler,
                                   Map<String, Object> attributes) {
        if (request instanceof org.springframework.http.server.ServletServerHttpRequest servletRequest) {
            HttpSession session = servletRequest.getServletRequest().getSession(false);
            if (session != null) {
                //Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                if (authentication instanceof UsernamePasswordAuthenticationToken) {
                    String username = authentication.getName();
                    attributes.put("username", username);
                    attributes.put("profilePictureUrl", userService.findUsersByUsername(username).getProfilePictureUrl());
                } else if (authentication instanceof OAuth2AuthenticationToken token) {
                    System.out.println("san dep trai");
                    Map<String, Object> attribute = token.getPrincipal().getAttributes();
                    attributes.put("username", attribute.get("login"));
                    attributes.put("profilePictureUrl", attribute.get("avatar_url"));
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