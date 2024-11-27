package com.scar.lms.service.impl;

import com.scar.lms.entity.User;
import com.scar.lms.repository.UserRepository;
import com.scar.lms.service.GitHubOAuth2Service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

import static com.scar.lms.entity.Role.USER;

@SuppressWarnings("DuplicatedCode")
@Service
public class GitHubOAuth2ServiceImpl implements GitHubOAuth2Service {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public GitHubOAuth2ServiceImpl(final UserRepository userRepository,
                                   final BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    public User registerNewUser(OAuth2User oAuth2User) {
        Map<String, Object> attributes = oAuth2User.getAttributes();
        String username = (String) attributes.get("login");
        String userId = String.valueOf(attributes.get("id"));
        String displayName = (String) attributes.get("name");

        if (displayName == null) {
            displayName = username;
        }

        return getUser(username, displayName, userRepository, bCryptPasswordEncoder);
    }

    private User getUser(String username, String displayName, UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        Optional<User> existingUser = userRepository.findByUsername(username);
        if (existingUser.isPresent()) {
            return existingUser.get();
        }

        User newUser = new User();
        if (userRepository.findByUsername(username).isPresent()) {
            String time = String.valueOf(System.nanoTime());
            while (userRepository.findByUsername(username + time).isPresent()) {
                time = String.valueOf(System.nanoTime());
            }
            username = username + time;
        }
        newUser.setUsername(username);
        newUser.setDisplayName(displayName);
        newUser.setEmail("github" + username + "@gmail.com");
        newUser.setRole(USER);
        newUser.setPoints(0);
        newUser.setPassword(bCryptPasswordEncoder.encode(username + displayName));
        return userRepository.save(newUser);
    }
}
