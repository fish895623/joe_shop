package com.bit.joe.shoppingmall.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;

import com.bit.joe.shoppingmall.enums.UserRole;
import com.bit.joe.shoppingmall.jwt.JWTUtil;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final AuthenticationConfiguration authenticationConfiguration;
    private final JWTUtil jwtUtil;

    @Bean
    public static BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration)
            throws Exception {

        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(
                        request ->
                                request.requestMatchers("/user/get-all")
                                        .hasAuthority(UserRole.ADMIN.name())
                                        .requestMatchers("/category/create")
                                        .hasAuthority(UserRole.ADMIN.name())
                                        .requestMatchers("/product/create")
                                        .hasAuthority(UserRole.ADMIN.name())
                                        .requestMatchers("/api/user/get-all")
                                        .hasAuthority(UserRole.ADMIN.name())
                                        .anyRequest()
                                        .permitAll())
                .httpBasic(Customizer.withDefaults())
                .logout(
                        logout ->
                                logout.logoutUrl("/user/logout")
                                        .logoutSuccessUrl("/login")
                                        .invalidateHttpSession(true)
                                        .clearAuthentication(true)
                                        .deleteCookies("JSESSIONID")
                                        .addLogoutHandler(new SecurityContextLogoutHandler()))
                .build();
    }
}
