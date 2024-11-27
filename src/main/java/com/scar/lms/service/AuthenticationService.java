package com.scar.lms.service;

import com.scar.lms.entity.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

@SuppressWarnings("SameReturnValue")
public interface AuthenticationService extends UserDetailsService {

    boolean validateRegistration(String username, String password, String displayName, String email);

    boolean validateUsername(String username);

    boolean validatePassword(String password);

    boolean validateEmail(String email);

    boolean validateDisplayName(String displayName);

    boolean updatePassword(String username, String oldPassword, String newPassword);

    String encryptPassword(String password);

    @SuppressWarnings("unused")
    Collection<? extends GrantedAuthority> getAuthorities(String username);

    CompletableFuture<String> extractUsernameFromAuthentication(Authentication authentication);

    boolean validateEditProfile(User currentUser, String newUsername, String newDisplayName, String newEmail);

    CompletableFuture<User> getAuthenticatedUser(Authentication authentication);
}
