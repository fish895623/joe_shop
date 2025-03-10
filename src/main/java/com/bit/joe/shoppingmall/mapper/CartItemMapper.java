package com.bit.joe.shoppingmall.mapper;

import org.springframework.stereotype.Component;

import com.bit.joe.shoppingmall.dto.CartItemDto;
import com.bit.joe.shoppingmall.entity.CartItem;
import com.bit.joe.shoppingmall.repository.CartRepository;

@Component
public class CartItemMapper {
    static CartRepository cartRepository;

    public static CartItemDto toDto(CartItem data) {
        return CartItemDto.builder()
                .id(data.getId())
                .cartId(data.getCart().getId())
                .product(ProductMapper.toDto(data.getProduct()))
                .quantity(data.getQuantity())
                .price(data.getPrice())
                .build();
    }

    public static CartItem toEntity(CartItemDto data) {
        return CartItem.builder()
                .id(data.getId())
                .cart(
                        cartRepository
                                .findById(data.getCartId())
                                .orElseThrow(() -> new RuntimeException("Cart not found")))
                .product(ProductMapper.toEntity(data.getProduct()))
                .quantity(data.getQuantity())
                .price(data.getPrice())
                .build();
    }
}
