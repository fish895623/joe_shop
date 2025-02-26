package com.bit.joe.shoppingmall.mapper;

import org.springframework.stereotype.Component;

import com.bit.joe.shoppingmall.dto.CartItemDto;
import com.bit.joe.shoppingmall.entity.CartItem;

@Component
public class CartItemMapper {
    public static CartItemDto toDto(CartItem data) {
        return CartItemDto.builder()
                .id(data.getId())
                .cart(CartMapper.toDto(data.getCart()))
                .product(ProductMapper.toDto(data.getProduct()))
                .price(data.getPrice())
                .build();
    }

    public static CartItem toEntity(CartItemDto data) {
        return CartItem.builder()
                .id(data.getId())
                .cart(CartMapper.toEntity(data.getCart()))
                .product(ProductMapper.toEntity(data.getProduct()))
                .price(data.getPrice())
                .build();
    }
}
