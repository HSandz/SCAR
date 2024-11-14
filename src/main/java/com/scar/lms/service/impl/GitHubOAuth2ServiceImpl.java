package com.scar.lms.service.impl;

import com.scar.lms.entity.User;
import com.scar.lms.repository.UserRepository;
import com.scar.lms.service.GitHubOAuth2Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

import static com.scar.lms.service.impl.UserServiceImpl.getUser;

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
        String email = (String) attributes.get("email");
        String displayName = (String) attributes.get("name");

        if (displayName == null) {
            displayName = username;
        }

        return getUser(email, username, displayName, userRepository, bCryptPasswordEncoder);
    }
}
