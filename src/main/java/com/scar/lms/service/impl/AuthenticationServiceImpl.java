package com.scar.lms.service.impl;

import com.scar.lms.entity.Role;
import com.scar.lms.entity.User;
import com.scar.lms.exception.NotFoundException;
import com.scar.lms.repository.UserRepository;
import com.scar.lms.service.AuthenticationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    private static final int MIN_USERNAME_LENGTH = 6;
    private static final int MAX_USERNAME_LENGTH = 20;
    private static final int MIN_PASSWORD_LENGTH = 8;
    private static final int MAX_PASSWORD_LENGTH = 20;

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public AuthenticationServiceImpl(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    public boolean validateAuthentication(String username, String password) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        return userOptional.filter(value -> bCryptPasswordEncoder.matches(password, value.getPassword())).isPresent();
    }

    @Override
    public boolean validateRegistration(String username, String password,
                                        String displayName, String email) {
        return validateUsername(username)
                && validatePassword(password)
                && validateDisplayName(displayName)
                && validateEmail(email);
    }

    private boolean validateUsername(String username) {
        String usernameRegex = "^[A-Za-z][A-Za-z0-9_@#]{"
                + (MIN_USERNAME_LENGTH - 1) + ","
                + (MAX_USERNAME_LENGTH - 1) + "}$";
        if (!username.matches(usernameRegex)) {
            return false;
        }
        return userRepository.findByUsername(username).isEmpty();
    }

    private boolean validatePassword(String password) {
        return password.length() >= MIN_PASSWORD_LENGTH
                && password.length() <= MAX_PASSWORD_LENGTH;
    }

    private boolean validateEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$";
        if (!email.matches(emailRegex)) {
            return false;
        }
        return userRepository.findByEmail(email).isEmpty();
    }

    private boolean validateDisplayName(String displayName) {
        return displayName.length() >= MIN_USERNAME_LENGTH - 2
                && displayName.length() <= MAX_USERNAME_LENGTH;
    }

    @Override
    public boolean updatePassword(String username, String oldPassword, String newPassword) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isPresent()) {
            userOptional.get().setPassword(bCryptPasswordEncoder.encode(oldPassword));
            return true;
        }
        return false;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities(String username) {
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) {
            throw new NotFoundException("User not found");
        }
        Set<Role> roles = new HashSet<>();
        roles.add(user.getRole());
        return roles;
    }
}
