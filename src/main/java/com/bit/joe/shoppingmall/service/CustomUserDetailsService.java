package com.bit.joe.shoppingmall.service;

import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.bit.joe.shoppingmall.dto.CustomUserDetails;
import com.bit.joe.shoppingmall.entity.User;
import com.bit.joe.shoppingmall.repository.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {

        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Optional<User> userData = userRepository.findByEmail(username);

        if (userData.isPresent()) {
            return new CustomUserDetails(userData.orElse(null));
        }

        return null;
    }
}
