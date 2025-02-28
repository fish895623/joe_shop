package com.bit.joe.shoppingmall.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionFixationProtectionStrategy;

import com.bit.joe.shoppingmall.enums.UserRole;

import jakarta.annotation.PostConstruct;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomAuthenticationProvider customAuthenticationProvider;
    private final CustomDaoAuthenticationProvider customDaoAuthenticationProvider;

    public SecurityConfig(
            CustomAuthenticationProvider customAuthenticationProvider,
            CustomDaoAuthenticationProvider customDaoAuthenticationProvider) {
        this.customAuthenticationProvider = customAuthenticationProvider;
        this.customDaoAuthenticationProvider = customDaoAuthenticationProvider;
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
                                        .anyRequest()
                                        .permitAll())
                .httpBasic(Customizer.withDefaults())
                .sessionManagement(
                        session ->
                                session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                                        .sessionAuthenticationStrategy(
                                                sessionAuthenticationStrategy())
                )
                .logout(
                        logout ->
                                logout.logoutUrl("/user/logout")
                                        .logoutSuccessUrl("/login")
                                        .invalidateHttpSession(true))
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.authenticationProvider(customAuthenticationProvider);
        authenticationManagerBuilder.authenticationProvider(customDaoAuthenticationProvider);
        return authenticationManagerBuilder.build();
    }

    @Bean
    public static BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    // ======================================== undeveloped yet ========================================
    @Bean
    public SessionAuthenticationStrategy sessionAuthenticationStrategy() {
        return new SessionFixationProtectionStrategy();
    }

    @PostConstruct
    public void init() {
        SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_INHERITABLETHREADLOCAL);
    }

    // ======================================== undeveloped yet ========================================

}
