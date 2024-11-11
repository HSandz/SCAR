package com.scar.lms.service.impl;

import com.scar.lms.entity.User;
import com.scar.lms.repository.UserRepository;
import com.scar.lms.service.GithubOAuth2Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

import static com.scar.lms.entity.Role.USER;
import static com.scar.lms.service.impl.UserServiceImpl.getUser;

@Service
public class GithubOAuth2ServiceImpl implements GithubOAuth2Service {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public GithubOAuth2ServiceImpl(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    public User registerNewUser(OAuth2User oAuth2User) {
        Map<String, Object> attributes = oAuth2User.getAttributes();
        String username = (String) attributes.get("login");
        String email = (String) attributes.get("email");
        String displayName = (String) attributes.get("name");

        if (displayName == null) {
            displayName = username;
        }

        return getUser(email, username, displayName, userRepository, bCryptPasswordEncoder);
    }
}
