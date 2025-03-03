package com.bit.joe.shoppingmall.service.Impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.bit.joe.shoppingmall.dto.request.OrderRequest;
import com.bit.joe.shoppingmall.dto.response.Response;
import com.bit.joe.shoppingmall.entity.CartItem;
import com.bit.joe.shoppingmall.entity.Order;
import com.bit.joe.shoppingmall.entity.OrderItem;
import com.bit.joe.shoppingmall.enums.OrderStatus;
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

    public Response requestCancel(OrderRequest orderRequest) {
        Order order =
                orderRepository
                        .findById(orderRequest.getOrderId())
                        .orElseThrow(() -> new NotFoundException("Order not " + "found"));
        // get order object

        // if order status is over PREPARE status, return error message
        if (order.getStatus().ordinal() > OrderStatus.PREPARE.ordinal()) {
            return Response.builder().status(400).message("Order cannot be cancelled").build();
        }

        orderRequest.setStatus(OrderStatus.CANCEL_REQUESTED);
        changeOrderStatus(orderRequest);
        // change order status

        return Response.builder().status(200).message("Order cancel requested").build();
        // return success message
    }

    public Response confirmCancel(OrderRequest orderRequest) {
        Order order =
                orderRepository
                        .findById(orderRequest.getOrderId())
                        .orElseThrow(() -> new NotFoundException("Order not " + "found"));
        // get order object

        // if order status is not CANCEL_REQUESTED, return error message
        if (order.getStatus() != OrderStatus.CANCEL_REQUESTED) {
            return Response.builder()
                    .status(400)
                    .message("Order cannot be cancelled without cancel request.")
                    .build();
        }

        orderRequest.setStatus(OrderStatus.CANCELLED);
        changeOrderStatus(orderRequest);
        // change order status

        return Response.builder().status(200).message("Order cancelled").build();
        // return success message
    }

    public Response requestReturn(OrderRequest orderRequest) {
        Order order =
                orderRepository
                        .findById(orderRequest.getOrderId())
                        .orElseThrow(() -> new NotFoundException("Order not " + "found"));
        // get order object

        // if order status is not DELIVERED, return error message
        if (order.getStatus() != OrderStatus.DELIVERED) {
            return Response.builder()
                    .status(400)
                    .message("Order cannot be returned without delivery.")
                    .build();
        }

        orderRequest.setStatus(OrderStatus.RETURN_REQUESTED);
        changeOrderStatus(orderRequest);
        // change order status

        return Response.builder().status(200).message("Order return requested").build();
        // return success message
    }

    public Response progressReturn(OrderRequest orderRequest) {
        Order order =
                orderRepository
                        .findById(orderRequest.getOrderId())
                        .orElseThrow(() -> new NotFoundException("Order not " + "found"));
        // get order object

        // if order status is not RETURN_REQUESTED, return error message
        if (order.getStatus() != OrderStatus.RETURN_REQUESTED) {
            return Response.builder()
                    .status(400)
                    .message("Order cannot be returned without return request.")
                    .build();
        }

        orderRequest.setStatus(OrderStatus.RETURN_IN_PROGRESS);
        changeOrderStatus(orderRequest);
        // change order status

        return Response.builder()
                .status(200)
                .message("Order return request now progressing")
                .build();
        // return success message
    }

    public Response completeReturn(OrderRequest orderRequest) {
        Order order =
                orderRepository
                        .findById(orderRequest.getOrderId())
                        .orElseThrow(() -> new NotFoundException("Order not " + "found"));
        // get order object

        // if order status is not RETURN_IN_PROGRESS, return error message
        if (order.getStatus() != OrderStatus.RETURN_IN_PROGRESS) {
            return Response.builder()
                    .status(400)
                    .message("Order cannot be returned without return request.")
                    .build();
        }

        orderRequest.setStatus(OrderStatus.RETURNED);
        changeOrderStatus(orderRequest);
        // change order status

        return Response.builder().status(200).message("Order returned").build();
        // return success message
    }
}
