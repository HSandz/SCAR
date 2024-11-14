package com.scar.lms.service.impl.oauth2;

import com.scar.lms.service.GithubOAuth2Service;
import com.scar.lms.service.GoogleOAuth2Service;

import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final GoogleOAuth2Service googleOAuth2Service;
    private final GithubOAuth2Service githubOAuth2Service;

    @Autowired
    public CustomOAuth2UserService(GoogleOAuth2Service googleOAuth2Service, GithubOAuth2Service githubOAuth2Service) {
        this.googleOAuth2Service = googleOAuth2Service;
        this.githubOAuth2Service = githubOAuth2Service;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        if ("google".equalsIgnoreCase(registrationId)) {
            googleOAuth2Service.registerNewUser(oAuth2User);
        } else if ("github".equalsIgnoreCase(registrationId)) {
            githubOAuth2Service.registerNewUser(oAuth2User);
        }

        return oAuth2User;
    }
}
