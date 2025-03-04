package com.bit.joe.shoppingmall.mapper;

import org.springframework.stereotype.Component;

import com.bit.joe.shoppingmall.dto.OrderDto;
import com.bit.joe.shoppingmall.dto.OrderItemDto;
import com.bit.joe.shoppingmall.entity.CartItem;
import com.bit.joe.shoppingmall.entity.Order;
import com.bit.joe.shoppingmall.entity.OrderItem;

@Component
public class OrderItemMapper {
    public static OrderItemDto toDto(OrderItem data) {
        return OrderItemDto.builder()
                .id(data.getId())
                .product(ProductMapper.toDto(data.getProduct()))
                .quantity(data.getQuantity())
                .price(data.getPrice())
                .orderId(data.getOrder().getId())
                .build();
    }

    public static OrderItem toEntity(OrderItemDto data) {
        return OrderItem.builder()
                .id(data.getId())
                .product(ProductMapper.toEntity(data.getProduct()))
                .quantity(data.getQuantity())
                .price(data.getPrice())
                .order(OrderMapper.toEntity(OrderDto.builder().id(data.getOrderId()).build()))
                .build();
    }

    public static OrderItem cartItemToOrderItem(CartItem cartItem, Order order) {
        return OrderItem.builder()
                .product(cartItem.getProduct())
                .quantity(cartItem.getQuantity())
                .price(cartItem.getPrice())
                .order(order)
                .build();
    }
}
