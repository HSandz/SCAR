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
        String username = (String) attributes.get("login"); // GitHub's username
        String userId = String.valueOf(attributes.get("id")); // GitHub's unique ID
        String displayName = (String) attributes.get("name");

        if (displayName == null) {
            displayName = username;
        }

        return getUser(userId, username, displayName, userRepository, bCryptPasswordEncoder);
    }

    private User getUser(String userId, String username, String displayName, UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
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
        // Default email to bypass non-null constraint
        newUser.setEmail("github" + username + "@gmail.com");
        newUser.setRole(USER);
        newUser.setPoints(0);
        // Default password to bypass non-null constraint
        newUser.setPassword(bCryptPasswordEncoder.encode(username + displayName));
        return userRepository.save(newUser);
    }
}
