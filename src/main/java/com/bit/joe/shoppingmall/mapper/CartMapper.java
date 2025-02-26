package com.bit.joe.shoppingmall.mapper;

import org.springframework.stereotype.Component;

import com.bit.joe.shoppingmall.dto.CartDto;
import com.bit.joe.shoppingmall.entity.Cart;

@Component
public class CartMapper {

    public static CartDto cartToDto(Cart data) {
        CartDto cartDto = new CartDto();
        cartDto.setId(data.getId());
        return cartDto;
    }
}
