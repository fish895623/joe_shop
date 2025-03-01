package com.bit.joe.shoppingmall.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.bit.joe.shoppingmall.entity.CartItem;
import com.bit.joe.shoppingmall.entity.User;
import com.bit.joe.shoppingmall.enums.OrderStatus;
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
public class OrderDto {
    private Long id;
    private User user;
    private String orderDate;
    private OrderStatus status;
    private LocalDateTime createdAt;
    private List<CartItem> orderItems; // 수정: orderLists -> orderItems
}
