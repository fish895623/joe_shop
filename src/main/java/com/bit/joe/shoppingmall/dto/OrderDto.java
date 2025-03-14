package com.bit.joe.shoppingmall.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.bit.joe.shoppingmall.enums.OrderStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor
@NoArgsConstructor
public class OrderDto {
    private Long id;
    private UserDto user;
    private LocalDateTime orderDate;
    private OrderStatus status;
    private List<OrderItemDto> orderItems; // 수정: orderLists -> orderItems
}
