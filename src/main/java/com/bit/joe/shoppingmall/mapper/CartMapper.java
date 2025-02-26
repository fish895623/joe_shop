package com.bit.joe.shoppingmall.mapper;

import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.bit.joe.shoppingmall.dto.CartDto;
import com.bit.joe.shoppingmall.entity.Cart;

@Component
public class CartMapper {

    public static CartDto toDto(Cart data) {
        return CartDto.builder()
                .id(data.getId())
                .user(UserMapper.toDto(data.getUser()))
                .cartItemDto(
                        data.getCartItems().stream()
                                .map(CartItemMapper::toDto)
                                .collect(Collectors.toList()))
                .createdAt(data.getCreatedAt())
                .build();
    }

    public static Cart toEntity(CartDto data) {
        return Cart.builder()
                .id(data.getId())
                .user(UserMapper.toEntity(data.getUser()))
                .cartItems(
                        data.getCartItemDto().stream()
                                .map(CartItemMapper::toEntity)
                                .collect(Collectors.toList()))
                .createdAt(data.getCreatedAt())
                .build();
    }
}
