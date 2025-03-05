package com.bit.joe.shoppingmall.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
                                        .maximumSessions(1) // 동시 로그인 세션 수 제한
                                        .expiredUrl("/login?expired=true")) // 세션 만료 시 리디렉션 URL
//                .rememberMe(rememberMe ->
//                        rememberMe.key("common-cookie-key") // remember-me 쿠키 키
//                                .tokenValiditySeconds(1209600)) // 14일 동안 로그인 유지
                .logout(
                        logout ->
                                logout.logoutUrl("/user/logout")
                                        .logoutSuccessUrl("/login")
                                        .invalidateHttpSession(true)// 로그아웃 시 세션 무효화
                                        .clearAuthentication(true) // 인증 정보 초기화
                                        .deleteCookies("JSESSIONID")) // JSESSIONID 쿠키 삭제
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.authenticationProvider(customAuthenticationProvider);
        authenticationManagerBuilder.authenticationProvider(customDaoAuthenticationProvider);
        return authenticationManagerBuilder.build();
        // Provider의 authenticate() 메서드에서 전달된 토큰을 검증 -> 인증이 성공하면 Authentication 객체를 반환(SecurityContext에 저장)
    }

    @Bean
    public static BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    // ======================================== undeveloped yet
    // ========================================
    @Bean
    public SessionAuthenticationStrategy sessionAuthenticationStrategy() {
        return new SessionFixationProtectionStrategy();
    }

    @PostConstruct
    public void init() {
        SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_INHERITABLETHREADLOCAL);
    }

    // ======================================== undeveloped yet
    // ========================================

}
