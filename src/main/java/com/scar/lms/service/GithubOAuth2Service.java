package com.scar.lms.service;

import com.scar.lms.entity.User;
import org.springframework.security.oauth2.core.user.OAuth2User;

public interface GithubOAuth2Service {

    User registerNewUser(OAuth2User oAuth2User);

}
