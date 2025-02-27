package com.bit.joe.shoppingmall.security;

import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.bit.joe.shoppingmall.entity.User;
import com.bit.joe.shoppingmall.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) { // Use isEmpty() to check if the user was found
            System.out.println("User Not Found");
            throw new UsernameNotFoundException("user not found");
        }

        return new CustomUserDetails(user.get()); // Get the actual User object from Optional
    }
}
