package com.bit.joe.shoppingmall.dto.request;

import com.bit.joe.shoppingmall.enums.UserGender;
import com.bit.joe.shoppingmall.enums.UserRole;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateUserInfoRequest {
    private Long id;
    private String name;
    private UserGender gender;
    private UserRole role;
    private String birth;
    private String email;
    private String currentPassword;
    private String confirmPassword;
    private String newPassword;
    private boolean active;

    private String phone;
    private String address;
}
