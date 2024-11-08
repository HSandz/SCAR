package com.scar.lms.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

@Configuration
public class SecurityConfiguration {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                        .requestMatchers("/",
                                "/register",
                                "/login",
                                "/css/**",
                                "/media/**",
                                "/static/**").permitAll()
                        .requestMatchers("/books/**", "/home").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")
                        .requestMatchers("/admin/**", "/home").hasAuthority("ROLE_ADMIN")
                        .anyRequest().authenticated()
                )
                .formLogin(formLogin -> formLogin
                        .loginPage("/login")
                        .usernameParameter("username")
                        .passwordParameter("password")
                        .permitAll())
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessHandler(userLogoutSuccessHandler())
                        .permitAll()
                );
        return http.build();
    }

    private LogoutSuccessHandler userLogoutSuccessHandler() {
        return (_, response, authentication) -> {
            response.sendRedirect("/login?logout=true");
            if (authentication != null) {
                System.out.printf("User %s logged out%n", authentication.getName());
            } else {
                System.out.println("Unable to logout due to null authentication");
            }
        };
    }
}
