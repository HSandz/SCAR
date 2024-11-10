package com.scar.lms.service.impl;

import com.scar.lms.repository.UserRepository;
import com.scar.lms.service.GoogleOAuth2RegistrationService;
import com.scar.lms.entity.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

import static com.scar.lms.entity.Role.USER;

@Service
public class GoogleOAuth2RegistrationServiceImpl implements GoogleOAuth2RegistrationService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public GoogleOAuth2RegistrationServiceImpl(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }


    public User registerNewUser(OAuth2User oAuth2User) {
        Map<String, Object> attributes = oAuth2User.getAttributes();
        String email = (String) attributes.get("email");
        String username = (String) attributes.get("given_name");
        String displayName = (String) attributes.get("name");

        Optional<User> existingUser = userRepository.findByEmail(email);
        if (existingUser.isPresent()) {
            return existingUser.get();
        }

        User newUser = new User();
        newUser.setUsername(username);
        newUser.setDisplayName(displayName);
        newUser.setEmail(email);
        newUser.setRole(USER);
        newUser.setPassword(bCryptPasswordEncoder.encode(email));
        return userRepository.save(newUser);
    }

}
