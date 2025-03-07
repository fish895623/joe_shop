package com.bit.joe.shoppingmall.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bit.joe.shoppingmall.dto.request.OrderRequest;
import com.bit.joe.shoppingmall.dto.response.Response;
import com.bit.joe.shoppingmall.service.Impl.OrderService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/create")
    public ResponseEntity<Response> createOrder(@RequestBody OrderRequest orderRequest) {
        Response resp = orderService.createOrder(orderRequest);

        return ResponseEntity.status(resp.getStatus()).body(resp);
    }

    @GetMapping("/get/{userId}/{orderId}")
    public ResponseEntity<Response> getOrder(
            @PathVariable Long userId, @PathVariable Long orderId) {
        Response resp = orderService.getOrder(userId, orderId);

        return ResponseEntity.status(resp.getStatus()).body(resp);
    }

    @GetMapping("/change-status")
    public ResponseEntity<Response> changeStatus(@RequestBody OrderRequest orderRequest) {
        Response resp = orderService.changeOrderStatus(orderRequest);

        return ResponseEntity.status(resp.getStatus()).body(resp);
    }

    @GetMapping("/request")
    public ResponseEntity<Response> requestHandle(@RequestBody OrderRequest orderRequest) {
        Response resp = orderService.progressOrderRequest(orderRequest);

        return ResponseEntity.status(resp.getStatus()).body(resp);
    }
}
