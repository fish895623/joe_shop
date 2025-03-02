package com.bit.joe.shoppingmall.service;

import com.bit.joe.shoppingmall.dto.OrderDto;
import com.bit.joe.shoppingmall.entity.Order;

import java.util.List;

public interface OrderService {

    Order placeOrder(Long userId);
    OrderDto getOrder(Long orderId);
    List<OrderDto> getUserOrders(Long userId);

}
