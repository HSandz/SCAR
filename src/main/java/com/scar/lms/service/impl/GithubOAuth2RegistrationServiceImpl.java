package com.scar.lms.service.impl;

import com.scar.lms.entity.User;
import com.scar.lms.repository.UserRepository;
import com.scar.lms.service.GithubOAuth2RegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

import static com.scar.lms.entity.Role.USER;

@Service
public class GithubOAuth2RegistrationServiceImpl implements GithubOAuth2RegistrationService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public GithubOAuth2RegistrationServiceImpl(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    public User registerNewUser(OAuth2User oAuth2User) {
        Map<String, Object> attributes = oAuth2User.getAttributes();
        String username = (String) attributes.get("login");
        String email = (String) attributes.get("email");
        String displayName = (String) attributes.get("name");

        // Fallback if the "name" attribute is null
        if (displayName == null) {
            displayName = username;
        }

        // Check if a user with this email already exists
        Optional<User> existingUser = userRepository.findByEmail(email);
        if (existingUser.isPresent()) {
            return existingUser.get();
        }

        // Create a new user if no existing user was found
        User newUser = new User();
        newUser.setUsername(username);
        newUser.setDisplayName(displayName);
        newUser.setEmail(email);
        newUser.setRole(USER);
        newUser.setPassword(bCryptPasswordEncoder.encode(email));

        return userRepository.save(newUser);
    }
}
