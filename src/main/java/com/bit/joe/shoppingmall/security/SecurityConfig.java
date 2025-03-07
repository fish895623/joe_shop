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
import org.springframework.security.web.context.*;

import com.bit.joe.shoppingmall.enums.UserRole;
import com.bit.joe.shoppingmall.security.handler.CustomLogoutSuccessHandler;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Slf4j
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
    public SecurityContextRepository securityContextRepository() {
        return new DelegatingSecurityContextRepository(
                new RequestAttributeSecurityContextRepository(),
                new HttpSessionSecurityContextRepository());
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.csrf(AbstractHttpConfigurer::disable)
                //                .addFilterAfter(new
                // SecurityContextPersistenceFilter(securityContextRepository()),
                // SecurityContextPersistenceFilter.class)
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
                                        .maximumSessions(1)
                                        .expiredUrl("/login?expired=true"))
                .formLogin(
                        httpSecurityFormLoginConfigurer -> {
                            log.info("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
                            httpSecurityFormLoginConfigurer
                                    .loginPage("/user/login")
                                    .usernameParameter("email")
                                    .passwordParameter("password");
                        })
                .logout(
                        logout ->
                                logout.logoutUrl("/logout")
                                        .logoutSuccessHandler(customLogoutSuccessHandler())
                                        .invalidateHttpSession(true)
                                        .clearAuthentication(true)
                                        .deleteCookies("JSESSIONID"))
                .build();
    }

    @Bean
    public CustomLogoutSuccessHandler customLogoutSuccessHandler() {
        return new CustomLogoutSuccessHandler();
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

    @PostConstruct
    public void init() {
        SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_INHERITABLETHREADLOCAL);
    }
}
