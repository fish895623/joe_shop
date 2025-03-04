package com.bit.joe.shoppingmall.service.Impl;

import static com.bit.joe.shoppingmall.enums.OrderStatus.*;
import static com.bit.joe.shoppingmall.enums.RequestType.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.bit.joe.shoppingmall.dto.request.OrderRequest;
import com.bit.joe.shoppingmall.dto.response.Response;
import com.bit.joe.shoppingmall.entity.CartItem;
import com.bit.joe.shoppingmall.entity.Order;
import com.bit.joe.shoppingmall.entity.OrderItem;
import com.bit.joe.shoppingmall.enums.OrderStatus;
import com.bit.joe.shoppingmall.enums.RequestType;
import com.bit.joe.shoppingmall.exception.NotFoundException;
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

        // 1. 주문 생성
        Order order =
                Order.builder()
                        .user(
                                userRepository
                                        .findById(orderRequest.getUserId())
                                        .orElseThrow(() -> new RuntimeException("User not found")))
                        .status(OrderStatus.ORDER)
                        .orderDate(orderRequest.getOrderDate())
                        .build();
        orderRepository.save(order);

        Order orderSaved =
                orderRepository
                        .findByOrderDateAndUserId(
                                orderRequest.getOrderDate(), orderRequest.getUserId())
                        .orElseThrow(() -> new RuntimeException("Order not found"));

        // 2. 주문 목록 생성
        List<OrderItem> orderItems =
                orderRequest.getCartItemIds().stream()
                        .map(
                                cartItemId -> {
                                    // DB에서 해당하는 각 cartItemId 조회
                                    CartItem cartItem =
                                            cartItemRepository
                                                    .findById(cartItemId)
                                                    .orElseThrow(
                                                            () ->
                                                                    new RuntimeException(
                                                                            "Cart item not found"));
                                    // 해당 cart에서 cartItems 삭제
                                    cartItemRepository.delete(cartItem);
                                    // 2. order items 생성
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

    public Response changeOrderStatus(OrderRequest orderRequest) {

        Order order =
                orderRepository
                        .findById(orderRequest.getOrderId())
                        .orElseThrow(() -> new NotFoundException("Order not found"));
        // get order object

        order.setStatus(orderRequest.getStatus());
        // change order status

        orderRepository.save(order);
        // save order object

        return Response.builder().status(200).message("Order status changed").build();
        // return success message
    }

    // --------------- isConditionOk ---------------
    /** {@summary} Check if the condition for request is ok */
    public Boolean isConditionOk(OrderStatus orderStatus, RequestType requestType) {
        Map<OrderStatus, RequestType> mapping = new HashMap<>();
        mapping.put(CONFIRM, REQUEST_CANCEL);
        mapping.put(ORDER, REQUEST_CANCEL);
        mapping.put(PREPARE, REQUEST_CANCEL);
        mapping.put(CANCEL_REQUESTED, CONFIRM_CANCEL);
        mapping.put(DELIVERED, REQUEST_RETURN);
        mapping.put(RETURN_REQUESTED, PROGRESS_RETURN);
        mapping.put(RETURN_IN_PROGRESS, COMPLETE_RETURN);

        return requestType == mapping.get(orderStatus);
    }

    // --------------- progressOrderRequest ---------------
    /** {@summary} Progress an order requests */
    public Response progressOrderRequest(OrderRequest orderRequest) {

        Order order =
                orderRepository
                        .findById(orderRequest.getOrderId())
                        .orElseThrow(() -> new NotFoundException("Order not found"));
        // get order object

        boolean isConditionOk = isConditionOk(order.getStatus(), orderRequest.getRequestType());
        // check if condition is ok

        if (!isConditionOk) {
            return Response.builder().status(400).message("Request type is not valid").build();
        }
        // return error message if condition is not ok

        Map<RequestType, OrderStatus> mapping = new HashMap<>();

        mapping.put(REQUEST_CANCEL, CANCEL_REQUESTED);
        mapping.put(CONFIRM_CANCEL, CANCELLED);
        mapping.put(REQUEST_RETURN, RETURN_REQUESTED);
        mapping.put(PROGRESS_RETURN, RETURN_IN_PROGRESS);
        mapping.put(COMPLETE_RETURN, RETURNED);

        OrderStatus newStatus = mapping.get(orderRequest.getRequestType());
        if (newStatus == null) {
            return Response.builder().status(400).message("Invalid request type").build();
        }
        // get new status from request type, return error message if request type is invalid

        orderRequest.setStatus(newStatus);
        changeOrderStatus(orderRequest);
        // switch case for request type

        return Response.builder().status(200).message("Order request progress").build();
        // return success message
    }
}
