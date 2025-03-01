package com.bit.joe.shoppingmall.security;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import com.bit.joe.shoppingmall.entity.User;
import com.bit.joe.shoppingmall.exception.NotFoundException;
import com.bit.joe.shoppingmall.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication)
            throws AuthenticationException {
        // 1. 데이터 베이스에서부터 UserDetails 사항을 불러와야 함
        // 2. 비밀번호 비교

        String username = authentication.getName();
        // username 불러오기 (우리 DB에서는 email을 username으로 사용)
        String pwd = authentication.getCredentials().toString();
        // password 불러오기
        User user =
                userRepository
                        .findByEmail(username)
                        .orElseThrow(() -> new NotFoundException("User data not found"));
        // db에서 username(->email) 문자열을 가진 User가 있으면 user로 반환

        // user가 존재한다면
        if (user != null) {
            // 비밀번호가 일치하다면
            if (bCryptPasswordEncoder.matches(pwd, user.getPassword())) {

                List<GrantedAuthority> authorities = new ArrayList<>();
                // 엔드 유저의 authorities 세부 사항을 덧붙임
                authorities.add(new SimpleGrantedAuthority(user.getRole().name()));
                // users 테이블 내의 role 컬럼에 존재, Role 문자열 값을 SimpleGrantedAuthority 클래스로 변환
                return new UsernamePasswordAuthenticationToken(username, pwd, authorities);
                // UsernamePasswordAuthenticationToken 대상을 새롭게 생성 후 반환
            }
            // 비밀번호가 일치하지 않다면
            else {
                throw new BadCredentialsException("Invalid password!");
            }
        }
        // user가 존재하지 않다면
        else {
            throw new BadCredentialsException("No user registered with this details!");
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return (UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication));
    }
}
