package com.bit.joe.shoppingmall.dto;

import com.bit.joe.shoppingmall.enums.UserGender;
import com.bit.joe.shoppingmall.enums.UserRole;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

    private Long id;
    private String name;
    private UserGender gender;
    private UserRole role;
    private String birth;
    private String email;
    private String password;

    // Order, Cart 파트는 아직 구현 전이라 주석 처리
    // private List<OrderDto> orders;
    // private List<CartDto> carts;

}
