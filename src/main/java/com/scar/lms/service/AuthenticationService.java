package com.scar.lms.service;

import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public interface AuthenticationService {

    boolean validateAuthentication(String username, String password);

    boolean updatePassword(String username, String oldPassword, String newPassword);

    Collection<? extends GrantedAuthority> getAuthorities(String username);
}
