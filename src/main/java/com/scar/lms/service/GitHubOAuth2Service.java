package com.scar.lms.service;

import org.springframework.security.oauth2.core.user.OAuth2User;

public interface GitHubOAuth2Service {

    void registerNewUser(OAuth2User oAuth2User);
}
