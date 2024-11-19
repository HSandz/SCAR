package com.scar.lms.service;

import com.scar.lms.entity.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Collection;

public interface AuthenticationService extends UserDetailsService {

    boolean validateRegistration(String username, String password, String displayName, String email);

    boolean updatePassword(String username, String oldPassword, String newPassword);

    String encryptPassword(String password);

    Collection<? extends GrantedAuthority> getAuthorities(String username);

    String extractUsernameFromAuthentication(Authentication authentication);

    boolean validateEditProfile(User currentUser, String newUsername, String newDisplayName, String newEmail);
}
