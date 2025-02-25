package com.bit.joe.shoppingmall.security;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable) // CSRF 비활성화 (주의: 보안상 필요한 경우 CSRF 토큰 사용)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/user/**").permitAll()
                .anyRequest().permitAll()
            )
            .formLogin(withDefaults());

        return http.build();
    }
}
