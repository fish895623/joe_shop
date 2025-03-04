package com.bit.joe.shoppingmall.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.bit.joe.shoppingmall.dto.response.Response;
import com.bit.joe.shoppingmall.service.Impl.CartService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping("/create/{userId}")
    public ResponseEntity<Response> createCart(@PathVariable Long userId) {

        return ResponseEntity.ok(cartService.createCart(userId));
        // return success response with status code 200 (OK)
    }

    @GetMapping("/add/{userId}/{productId}/{quantity}")
    public ResponseEntity<Response> appendProductToCart(
            @PathVariable Long userId, @PathVariable Long productId, @PathVariable int quantity) {

        return ResponseEntity.ok(cartService.appendProductToCart(userId, productId, quantity));
        // return success response with status code 200 (OK)
    }

    @PostMapping("/remove/{cartId}/{productId}")
    public ResponseEntity<Response> removeProductFromCart(
            @PathVariable Long cartId, @PathVariable Long productId) {

        Response resp = cartService.removeProductFromCart(cartId, productId);
        // Remove product from cart and get response

        return ResponseEntity.ok(resp);
        // return success response with status code 200 (OK)
    }
}
