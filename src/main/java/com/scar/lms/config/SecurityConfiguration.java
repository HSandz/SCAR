package com.scar.lms.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

@Configuration
public class SecurityConfiguration {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                        .requestMatchers("/",
                                "/register",
                                "/css/**",
                                "/image/**",
                                "/static/**").permitAll()
                        .requestMatchers("/books/**", "/home").hasAnyAuthority("USER", "ADMIN")
                        .requestMatchers("/admin/**").hasAuthority("ADMIN")
                        .anyRequest().authenticated()
                )
                .formLogin(formLogin -> formLogin
                        .loginPage("/login").permitAll())
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
