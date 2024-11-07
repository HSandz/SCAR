package com.scar.lms.service;

import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public interface AuthenticationService {

    boolean validateAuthentication(String username, String password);

    boolean validateRegistration(String username, String password, String displayName, String email);

    boolean updatePassword(String username, String oldPassword, String newPassword);

    String encryptPassword(String password);

    Collection<? extends GrantedAuthority> getAuthorities(String username);
}
