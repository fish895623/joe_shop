package com.bit.joe.shoppingmall.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.bit.joe.shoppingmall.dto.JoinDTO;
import com.bit.joe.shoppingmall.entity.User;
import com.bit.joe.shoppingmall.enums.UserRole;
import com.bit.joe.shoppingmall.repository.UserRepository;

@Service
public class JoinService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public JoinService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {

        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public void joinProcess(JoinDTO joinDTO) {

        String username = joinDTO.getUsername();
        String password = joinDTO.getPassword();

        Boolean isExist = userRepository.existsByEmail(username);

        if (isExist) {

            return;
        }

        User data = new User();

        data.setEmail(username);
        data.setPassword(bCryptPasswordEncoder.encode(password));
        data.setRole(UserRole.ADMIN);

        userRepository.save(data);
    }
}
