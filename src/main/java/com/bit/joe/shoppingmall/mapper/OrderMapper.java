package com.bit.joe.shoppingmall.mapper;

import org.springframework.stereotype.Component;

import com.bit.joe.shoppingmall.dto.OrderDto;
import com.bit.joe.shoppingmall.entity.Order;

@Component
public class OrderMapper {
    public static OrderDto toDto(Order data) {

        return OrderDto.builder()
                .id(data.getId())
                .user(data.getUser())
                .orderDate(data.getOrderDate())
                .status(data.getStatus())
                .orderItems(data.getOrderItems())
                .build();
    }

    public static Order toEntity(OrderDto data) {
        return Order.builder()
                .id(data.getId())
                .user(data.getUser())
                .orderDate(data.getOrderDate())
                .status(data.getStatus())
                .orderItems(data.getOrderItems())
                .build();
    }
}
