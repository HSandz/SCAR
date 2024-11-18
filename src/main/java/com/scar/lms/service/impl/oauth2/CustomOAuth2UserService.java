package com.scar.lms.service.impl.oauth2;

import com.scar.lms.exception.InvalidDataException;
import com.scar.lms.exception.OperationNotAllowedException;
import com.scar.lms.service.GitHubOAuth2Service;
import com.scar.lms.service.GoogleOAuth2Service;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final GoogleOAuth2Service googleOAuth2Service;
    private final GitHubOAuth2Service githubOAuth2Service;

    public CustomOAuth2UserService(final GoogleOAuth2Service googleOAuth2Service,
                                   final GitHubOAuth2Service githubOAuth2Service) {
        this.googleOAuth2Service = googleOAuth2Service;
        this.githubOAuth2Service = githubOAuth2Service;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        if (userRequest == null || userRequest.getClientRegistration() == null) {
            throw new InvalidDataException("User request or client registration is null");
        }

        OAuth2User oAuth2User = super.loadUser(userRequest);
        if (oAuth2User == null) {
            throw new InvalidDataException("OAuth2 user is null");
        }

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        if (registrationId == null) {
            throw new InvalidDataException("Registration ID is null");
        }

        List<SimpleGrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
        String defaultSubAttribute = "sub";

        if ("google".equalsIgnoreCase(registrationId)) {
            googleOAuth2Service.registerNewUser(oAuth2User);
        } else if ("github".equalsIgnoreCase(registrationId)) {
            githubOAuth2Service.registerNewUser(oAuth2User);
            defaultSubAttribute = "id";
        } else {
            throw new OperationNotAllowedException("Unable to log in");
        }

        return new DefaultOAuth2User(authorities, oAuth2User.getAttributes(), defaultSubAttribute);
    }

}
