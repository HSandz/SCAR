package com.scar.lms.service.impl.oauth2;

import com.scar.lms.service.GithubOAuth2RegistrationService;
import com.scar.lms.service.GoogleOAuth2RegistrationService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CustomOAuth2UserServiceImpl extends DefaultOAuth2UserService {

    private final GoogleOAuth2RegistrationService googleOAuth2RegistrationService;
    private final GithubOAuth2RegistrationService githubOAuth2RegistrationService;

    @Autowired
    public CustomOAuth2UserServiceImpl(GoogleOAuth2RegistrationService googleOAuth2RegistrationService, GithubOAuth2RegistrationService githubOAuth2RegistrationService) {
        this.googleOAuth2RegistrationService = googleOAuth2RegistrationService;
        this.githubOAuth2RegistrationService = githubOAuth2RegistrationService;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        if ("google".equalsIgnoreCase(registrationId)) {
            googleOAuth2RegistrationService.registerNewUser(oAuth2User);
        } else if ("github".equalsIgnoreCase(registrationId)) {
            githubOAuth2RegistrationService.registerNewUser(oAuth2User);
        }

        return oAuth2User;
    }
}
