package com.bit.joe.shoppingmall.service.Impl;

import static com.bit.joe.shoppingmall.enums.OrderStatus.*;
import static com.bit.joe.shoppingmall.enums.RequestType.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.bit.joe.shoppingmall.dto.OrderDto;
import com.bit.joe.shoppingmall.dto.request.OrderRequest;
import com.bit.joe.shoppingmall.dto.response.Response;
import com.bit.joe.shoppingmall.entity.CartItem;
import com.bit.joe.shoppingmall.entity.Order;
import com.bit.joe.shoppingmall.entity.OrderItem;
import com.bit.joe.shoppingmall.entity.User;
import com.bit.joe.shoppingmall.enums.OrderStatus;
import com.bit.joe.shoppingmall.enums.RequestType;
import com.bit.joe.shoppingmall.exception.NotFoundException;
import com.bit.joe.shoppingmall.mapper.OrderItemMapper;
import com.bit.joe.shoppingmall.mapper.OrderMapper;
import com.bit.joe.shoppingmall.repository.CartItemRepository;
import com.bit.joe.shoppingmall.repository.OrderItemRepository;
import com.bit.joe.shoppingmall.repository.OrderRepository;
import com.bit.joe.shoppingmall.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Service
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartItemRepository cartItemRepository;
    private final UserService userService;

    /**
     * {@summary} Create an order
     *
     * @param orderRequest 주문 요청 객체
     * @return Response
     */
    public Response createOrder(OrderRequest orderRequest) {

        // make order entity and save it
        Order order =
                Order.builder()
                        .user(userService.getLoginUser()) // get logged-in user
                        .status(OrderStatus.ORDER) // set order status to ORDER
                        .orderDate(orderRequest.getOrderDate()) // set order date to now
                        .build();
        Order orderSaved = orderRepository.save(order);

        // Make orderItems after saving order entity, because orderItem needs orderId to be saved.
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

        // save order items
        orderItemRepository.saveAll(orderItems);

        // return success message
        return Response.builder().status(200).message("Order created successfully").build();
    }

    /**
     * {@summary} Change order status
     *
     * @param orderRequest 주문 요청 객체
     */
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

    /**
     * {@summary} Check if condition for progress request is ok
     *
     * @param orderStatus 주문 상태
     * @param requestType 요청 타입
     * @return Boolean
     */
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

    /**
     * {@summary} Progress order request
     *
     * @param orderRequest 주문 요청 객체
     * @return Response
     */
    public Response progressOrderRequest(OrderRequest orderRequest) {

        // get order object
        Order order =
                orderRepository
                        .findById(orderRequest.getOrderId())
                        .orElseThrow(() -> new NotFoundException("Order not found"));

        // check if condition is ok
        boolean isConditionOk = isConditionOk(order.getStatus(), orderRequest.getRequestType());

        // return error message if condition is not ok
        if (!isConditionOk) {
            return Response.builder().status(400).message("Request type is not valid").build();
        }

        // hashmap for request type and new status
        Map<RequestType, OrderStatus> requestTypeOrderStatusHashMap = new HashMap<>();
        requestTypeOrderStatusHashMap.put(REQUEST_CANCEL, CANCEL_REQUESTED);
        requestTypeOrderStatusHashMap.put(CONFIRM_CANCEL, CANCELLED);
        requestTypeOrderStatusHashMap.put(REQUEST_RETURN, RETURN_REQUESTED);
        requestTypeOrderStatusHashMap.put(PROGRESS_RETURN, RETURN_IN_PROGRESS);
        requestTypeOrderStatusHashMap.put(COMPLETE_RETURN, RETURNED);

        // get new status from request type, return error message if request type is invalid
        OrderStatus newStatus = requestTypeOrderStatusHashMap.get(orderRequest.getRequestType());
        if (newStatus == null) {
            return Response.builder().status(400).message("Invalid request type").build();
        }

        // switch case for request type
        orderRequest.setStatus(newStatus);
        changeOrderStatus(orderRequest);

        // return success message
        return Response.builder().status(200).message("Order request progress").build();
    }

    /**
     * {@summary} Get order
     *
     * @param userId 유저 아이디
     * @param orderId 주문 아이디
     * @return Response
     */
    public Response getOrder(Long userId, Long orderId) {
        User loginUser = userService.getLoginUser();
        // get authentication from the context holder

        // compare userId from the context holder and userId from the request
        if (!userId.equals(loginUser.getId())) {
            return Response.builder().status(405).message("Can not get other user's order").build();
        }

        // get order object
        Order order = orderRepository.findById(orderId).orElse(null);

        // if order is null, return error message
        if (order == null) {
            return Response.builder().status(404).message("Order not found").build();
        }

        // Order Items setting
        List<OrderItem> orderItems = orderItemRepository.findByOrder(order);
        order.setOrderItems(orderItems);

        // Convert order to OrderDto
        OrderDto orderDto = OrderMapper.toDto(order);

        // return success message
        return Response.builder()
                .status(200)
                .message("Get order successfully")
                .order(orderDto)
                .build();
    }
}
