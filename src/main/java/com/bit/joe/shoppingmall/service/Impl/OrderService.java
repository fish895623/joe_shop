package com.bit.joe.shoppingmall.service.Impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.bit.joe.shoppingmall.dto.request.OrderRequest;
import com.bit.joe.shoppingmall.dto.response.Response;
import com.bit.joe.shoppingmall.entity.CartItem;
import com.bit.joe.shoppingmall.entity.Order;
import com.bit.joe.shoppingmall.entity.OrderItem;
import com.bit.joe.shoppingmall.enums.OrderStatus;
import com.bit.joe.shoppingmall.mapper.OrderItemMapper;
import com.bit.joe.shoppingmall.repository.CartItemRepository;
import com.bit.joe.shoppingmall.repository.OrderItemRepository;
import com.bit.joe.shoppingmall.repository.OrderRepository;
import com.bit.joe.shoppingmall.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Service
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserRepository userRepository;
    private final CartItemRepository cartItemRepository;

    public Response createOrder(OrderRequest orderRequest) {

        boolean isOrderExist =
                orderRepository
                        .findByOrderDateAndUserId(
                                orderRequest.getOrderDate(), orderRequest.getUserId())
                        .isPresent();
        // Check if order already exists

        if (isOrderExist) {
            return Response.builder().status(400).message("Order already exists").build();
        }
        // return error message if order already exists

        Order order =
                Order.builder()
                        .user(
                                userRepository
                                        .findById(orderRequest.getUserId())
                                        .orElseThrow(() -> new RuntimeException("User not found")))
                        .status(OrderStatus.ORDER)
                        .orderDate(orderRequest.getOrderDate())
                        .build();
        // create order object

        orderRepository.save(order);
        // save order object

        Order orderSaved =
                orderRepository
                        .findByOrderDateAndUserId(
                                orderRequest.getOrderDate(), orderRequest.getUserId())
                        .orElseThrow(() -> new RuntimeException("Order not found"));
        // get saved order object

        List<OrderItem> orderItems =
                orderRequest.getCartItemIds().stream()
                        .map(
                                cartItemId -> {
                                    CartItem cartItem =
                                            cartItemRepository
                                                    .findById(cartItemId)
                                                    .orElseThrow(
                                                            () ->
                                                                    new RuntimeException(
                                                                            "Cart item not found"));
                                    cartItemRepository.delete(cartItem);
                                    return OrderItemMapper.cartItemToOrderItem(
                                            cartItem, orderSaved);
                                })
                        .toList();
        // create order items from cart items

        orderItemRepository.saveAll(orderItems);
        // save order items

        return Response.builder().message("Order created successfully").build();
        // return success message
    }
}
