package com.bit.joe.shoppingmall.controller;

import com.bit.joe.shoppingmall.dto.Response;
import com.bit.joe.shoppingmall.service.Impl.CartService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Controller
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping("/add/{userId}/{cartId}/{productId}/{quantity}")
    public ResponseEntity<Response> appendProductToCart(
            @PathVariable Long userId,
            @PathVariable Long cartId,
            @PathVariable Long productId,
            @PathVariable int quantity) {
        log.info(
                "appendProductToCart: userId={}, cartId={}, productId={}, quantity={}",
                userId,
                cartId,
                productId,
                quantity);

        return ResponseEntity.ok(
                cartService.appendProductToCart(cartId, userId, productId, quantity));
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
