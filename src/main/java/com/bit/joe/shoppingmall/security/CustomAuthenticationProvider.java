package com.bit.joe.shoppingmall.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class CustomAuthenticationProvider extends DaoAuthenticationProvider {

    @Autowired
    public CustomAuthenticationProvider(
            UserDetailsService userDetailsService, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.setUserDetailsService(userDetailsService);
        this.setPasswordEncoder(bCryptPasswordEncoder);
    }
}
