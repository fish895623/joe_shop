package com.bit.joe.shoppingmall.security;

import java.util.Collection;
import java.util.List;

import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import com.bit.joe.shoppingmall.entity.User;
import com.bit.joe.shoppingmall.exception.NotFoundException;
import com.bit.joe.shoppingmall.repository.UserRepository;

@Component
public class CustomDaoAuthenticationProvider extends DaoAuthenticationProvider {

    public CustomDaoAuthenticationProvider(CustomUserDetailsService userDetailsService) {
        setUserDetailsService(userDetailsService);
    }

    public static class CustomUserDetail implements UserDetails {
        private final User user;

        public CustomUserDetail(User user) {
            this.user = user;
        }

        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            return List.of(new SimpleGrantedAuthority(user.getRole().name()));
        }

        @Override
        public String getPassword() {
            return user.getPassword();
        }

        @Override
        public String getUsername() {
            return user.getEmail();
        }

        @Override
        public boolean isAccountNonExpired() {
            return true;
        }

        @Override
        public boolean isAccountNonLocked() {
            return true;
        }

        @Override
        public boolean isCredentialsNonExpired() {
            return true;
        }

        @Override
        public boolean isEnabled() {
            return true;
        }
    }

    @Component
    public static class CustomUserDetailsService implements UserDetailsService {
        private final UserRepository userRepository;

        public CustomUserDetailsService(UserRepository userRepository) {
            this.userRepository = userRepository;
        }

        @Override
        public UserDetails loadUserByUsername(String email) {
            User user =
                    userRepository
                            .findByEmail(email)
                            .orElseThrow(() -> new NotFoundException("User not found"));
            return new CustomUserDetail(user);
        }
    }
}
