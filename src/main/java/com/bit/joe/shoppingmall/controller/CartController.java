package com.bit.joe.shoppingmall.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.bit.joe.shoppingmall.dto.response.Response;
import com.bit.joe.shoppingmall.service.Impl.CartService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping("/create/{userId}")
    public ResponseEntity<Response> createCart(
            @PathVariable Long userId) {

        return ResponseEntity.ok(cartService.createCart(userId));
        // return success response with status code 200 (OK)
    }

    @GetMapping("/add/{userId}/{productId}/{quantity}")
    public ResponseEntity<Response> appendProductToCart(
            @PathVariable Long userId, @PathVariable Long productId, @PathVariable int quantity) {
        log.info("cartId: {}, productId: {}, quantity: {}", userId, productId, quantity);

        return ResponseEntity.ok(cartService.appendProductToCart(userId, productId, quantity));
    }

    @PostMapping("/remove/{cartId}/{productId}")
    public ResponseEntity<Response> removeProductFromCart(
            @PathVariable Long cartId, @PathVariable Long productId) {
        cartService.removeProductFromCart(cartId, productId);

        return ResponseEntity.ok(
                Response.builder()
                        .status(200)
                        .message("Product removed from cart successfully")
                        .build());
    }
}
