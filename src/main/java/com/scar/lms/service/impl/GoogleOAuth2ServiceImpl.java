package com.scar.lms.service.impl;

import com.scar.lms.repository.UserRepository;
import com.scar.lms.service.GoogleOAuth2Service;
import com.scar.lms.entity.User;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

import static com.scar.lms.entity.Role.USER;

@Service
public class GoogleOAuth2ServiceImpl implements GoogleOAuth2Service {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public GoogleOAuth2ServiceImpl(final UserRepository userRepository,
                                   final BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public void registerNewUser(OAuth2User oAuth2User) {
        Map<String, Object> attributes = oAuth2User.getAttributes();
        String email = (String) attributes.get("email");
        String username = (String) attributes.get("given_name");
        String displayName = (String) attributes.get("name");

        if (displayName == null) {
            displayName = username;
        }

        getUser(email, username, displayName, userRepository, bCryptPasswordEncoder);
    }

    private void getUser(String email, String username, String displayName, UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {

        Optional<User> existingUser = userRepository.findByEmail(email);

        if (existingUser.isPresent()) {
            return;
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
        newUser.setEmail(email);
        newUser.setRole(USER);
        newUser.setPoints(0);
        newUser.setPassword(bCryptPasswordEncoder.encode(username + displayName));
        userRepository.save(newUser);
    }
}
