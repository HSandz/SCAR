package com.scar.lms.service.impl;

import com.scar.lms.entity.Role;
import com.scar.lms.entity.User;
import com.scar.lms.repository.UserRepository;
import com.scar.lms.service.AuthenticationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

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
        if (user != null) {
            Role role = user.getRole();
            if (role != null) {
                Set<Role> roles = new HashSet<>();
                roles.add(role);
                return roles;
            }
        } else {
            throw new UsernameNotFoundException("User not found");
        }
        return List.of();
    }
}
