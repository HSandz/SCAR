package com.scar.lms.config;

import com.scar.lms.service.AuthenticationService;
import com.scar.lms.service.impl.oauth2.CustomOAuth2UserService;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import java.util.Set;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    private final AuthenticationService authenticationService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final CustomOAuth2UserService customOAuth2UserService;

    public SecurityConfiguration(final AuthenticationService authenticationService,
                                 final BCryptPasswordEncoder bCryptPasswordEncoder,
                                 final CustomOAuth2UserService customOAuth2UserService) {
        this.authenticationService = authenticationService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.customOAuth2UserService = customOAuth2UserService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                        .requestMatchers("/",
                                "/home",
                                "/register",
                                "/login",
                                "/css/**",
                                "/js/**",
                                "/media/**",
                                "/static/**",
                                "/images/**",
                                "/favicon.ico").permitAll()
                        .requestMatchers("/books/**", "/user/**", "/chat").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")
                        .requestMatchers("/ws/**", "/app/**").authenticated()
                        .anyRequest().authenticated()
                )
                .formLogin(formLogin -> formLogin
                        .loginPage("/login")
                        .usernameParameter("username")
                        .passwordParameter("password")
                        .successHandler(roleBasedAuthenticationSuccessHandler())
                        .permitAll())
                .oauth2Login(oauth2Login -> oauth2Login
                        .loginPage("/login")
                        .successHandler(oauth2AuthenticationSuccessHandler())
                        .userInfoEndpoint(userInfoEndpointConfig -> userInfoEndpointConfig
                                .userService(customOAuth2UserService)
                        )
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessHandler(userLogoutSuccessHandler())
                        .permitAll()
                );
        return http.build();
    }

    @Bean
    public AuthenticationFailureHandler loginFailureHandler() {
        return (request, response, _) -> {
            request.getSession().setAttribute("error", "Incorrect username or password");
            response.sendRedirect("/login?error=true");
        };
    }

    @Bean
    public LogoutSuccessHandler userLogoutSuccessHandler() {
        return (_, response, authentication) -> {
            response.sendRedirect("/login?logout=true");
            if (authentication != null) {
                System.out.printf("User %s logged out%n", authentication.getName());
            } else {
                System.out.println("Unable to properly logout due to null authentication");
            }
        };
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(authenticationService);
        authProvider.setPasswordEncoder(bCryptPasswordEncoder);
        return authProvider;
    }

    @Bean
    public AuthenticationSuccessHandler roleBasedAuthenticationSuccessHandler() {
        return getAuthenticationSuccessHandler();
    }

    @Bean
    public AuthenticationSuccessHandler oauth2AuthenticationSuccessHandler() {
        return getAuthenticationSuccessHandler();
    }

    private AuthenticationSuccessHandler getAuthenticationSuccessHandler() {
        return (_, response, authentication) -> {
            Set<String> roles = AuthorityUtils.authorityListToSet(authentication.getAuthorities());
            if (roles.contains("ROLE_ADMIN")) {
                response.sendRedirect("/admin");
            } else if (roles.contains("ROLE_USER")) {
                response.sendRedirect("/books");
            } else {
                response.sendRedirect("/");
            }
        };
    }
}
