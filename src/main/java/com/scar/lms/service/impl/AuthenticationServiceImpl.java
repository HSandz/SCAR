package com.scar.lms.service.impl;

import com.scar.lms.entity.User;
import com.scar.lms.exception.InvalidDataException;
import com.scar.lms.exception.ResourceNotFoundException;
import com.scar.lms.repository.UserRepository;
import com.scar.lms.service.AuthenticationService;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AuthenticationServiceImpl implements AuthenticationService, UserDetailsService {

    private static final int MIN_USERNAME_LENGTH = 6;
    private static final int MAX_USERNAME_LENGTH = 20;
    private static final int MIN_PASSWORD_LENGTH = 8;
    private static final int MAX_PASSWORD_LENGTH = 20;

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public AuthenticationServiceImpl(final UserRepository userRepository,
                                     final BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    public String extractUsernameFromAuthentication(Authentication authentication) {
        if (authentication instanceof OAuth2AuthenticationToken token) {
            if (token.getPrincipal() == null || token.getPrincipal().getAttributes() == null) {
                throw new InvalidDataException("OAuth2 token principal or attributes are null");
            }
            Map<String, Object> attributes = token.getPrincipal().getAttributes();
            return (String) attributes.get("login");
        } else if (authentication instanceof UsernamePasswordAuthenticationToken) {
            return authentication.getName();
        } else {
            throw new InvalidDataException("Unsupported authentication type");
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws ResourceNotFoundException {
        User user = userRepository
                .findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User with username not found: " + username));
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                getAuthorities(user.getUsername())
        );
    }

    @Override
    public boolean validateRegistration(String username, String password,
                                        String displayName, String email) {
        return validateUsername(username) &&
                validatePassword(password) &&
                validateEmail(email) &&
                validateDisplayName(displayName);
    }

    private boolean validateUsername(String username) {
        String usernameRegex = "^[A-Za-z][A-Za-z0-9_@#]{" + (MIN_USERNAME_LENGTH - 1) + "," + (MAX_USERNAME_LENGTH - 1) + "}$";
        if (!username.matches(usernameRegex)) {
            System.err.println("Invalid username format: " + username);
            return false;
        }

        if (userRepository.findByUsername(username).isPresent()) {
            System.err.println("Username already exists: " + username);
            return false;
        }

        return true;
    }

    private boolean validatePassword(String password) {
        if (password.length() < MIN_PASSWORD_LENGTH || password.length() > MAX_PASSWORD_LENGTH) {
            System.err.println("Password length is invalid.");
            return false;
        }

        return true;
    }

    private boolean validateEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$";
        if (!email.matches(emailRegex)) {
            System.err.println("Invalid email format: " + email);
            return false;
        }

        if (userRepository.findByEmail(email).isPresent()) {
            System.err.println("Email already exists: " + email);
            return false;
        }

        return true;
    }

    private boolean validateDisplayName(String displayName) {
        if (displayName.length() < (MIN_USERNAME_LENGTH - 2) || displayName.length() > MAX_USERNAME_LENGTH) {
            System.err.println("Display name length is invalid.");
            return false;
        }
        return true;
    }

    @Override
    public boolean updatePassword(String username, String oldPassword, String newPassword) {
        if (!validatePassword(newPassword)) {
            System.err.println("Invalid new password.");
            return false;
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User with username not found: " + username));

        if (!bCryptPasswordEncoder.matches(oldPassword, user.getPassword())) {
            System.err.println("Old password does not match.");
            return false;
        }

        user.setPassword(bCryptPasswordEncoder.encode(newPassword));
        userRepository.save(user);
        return true;
    }

    @Override
    public String encryptPassword(String password) {
        return bCryptPasswordEncoder.encode(password);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User with username not found: " + username));

        Set<GrantedAuthority> authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
        System.out.println("Assigned authority: ROLE_" + user.getRole().name());
        return authorities;
    }

    @Override
    public String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return extractUsernameFromAuthentication(authentication);
    }

    @Override
    public boolean validateEditProfile(User user, User updatedUser) {
        boolean validUsername = false;
        boolean validDisplayName = false;
        if (validateUsername(updatedUser.getUsername())) {
            user.setUsername(updatedUser.getUsername());
            validUsername = true;
        }
        if (validateDisplayName(updatedUser.getDisplayName())) {
            user.setDisplayName(updatedUser.getDisplayName());
            validDisplayName = true;
        }
        return validUsername || validDisplayName;
    }
}
