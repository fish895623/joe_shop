package com.bit.joe.shoppingmall.entity;

import java.util.List;

import com.bit.joe.shoppingmall.enums.UserGender;
import com.bit.joe.shoppingmall.enums.UserRole;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Enumerated(EnumType.STRING) // Enum 값을 문자열로 저장
    @Column(name = "gender", nullable = false)
    private UserGender gender;

    @Enumerated(EnumType.STRING) // Enum 값을 문자열로 저장
    @Column(name = "role", nullable = false)
    private UserRole role;

    @Column(name = "birth", nullable = false)
    private String birth;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @OneToMany(mappedBy = "user")
    private List<Order> orders;

    @OneToOne(mappedBy = "user")
    private Cart cart;
}
