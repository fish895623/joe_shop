package com.bit.joe.shoppingmall.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.bit.joe.shoppingmall.dto.request.CartRequest;
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

    /**
     * Create a cart initially (when user registers)
     *
     * @return ResponseEntity<Response>
     */
    @GetMapping("/create")
    public ResponseEntity<Response> createCart() {

        return ResponseEntity.ok(cartService.createCart());
        // return success response with status code 200 (OK)
    }

    /**
     * Append a product to the cart
     *
     * @param cartRequest CartRequest
     * @return ResponseEntity<Response>
     */
    @GetMapping("/append")
    public ResponseEntity<Response> appendProductToCart(@RequestBody CartRequest cartRequest) {

        return ResponseEntity.ok(cartService.appendProductToCart(cartRequest));
        // return success response with status code 200 (OK)
    }

    /**
     * Remove a product from the cart
     *
     * @param cartRequest CartRequest
     * @return ResponseEntity<Response>
     */
    @PostMapping("/remove}")
    public ResponseEntity<Response> removeProductFromCart(@RequestBody CartRequest cartRequest) {

        Response resp = cartService.removeProductFromCart(cartRequest);
        // Remove product from cart and get response

        return ResponseEntity.ok(resp);
        // return success response with status code 200 (OK)
    }
}
