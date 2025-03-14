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
    @PostMapping("/create")
    public ResponseEntity<Response> createCart(@RequestBody CartRequest cartRequest) {

        return ResponseEntity.ok(cartService.createCart(cartRequest));
        // return success response with status code 200 (OK)
    }

    /**
     * Append a product to the cart
     *
     * @param cartRequest CartRequest
     * @return ResponseEntity<Response>
     */
    @PostMapping("/append")
    public ResponseEntity<Response> appendProductToCart(
            @CookieValue("token") String token, @RequestBody CartRequest cartRequest) {

        return ResponseEntity.ok(cartService.appendProductToCart(token, cartRequest));
        // return success response with status code 200 (OK)
    }

    /**
     * Remove a product from the cart
     *
     * @param cartRequest CartRequest
     * @return ResponseEntity<Response>
     */
    @PostMapping("/remove")
    public ResponseEntity<Response> removeProductFromCart(
            @CookieValue("token") String token, @RequestBody CartRequest cartRequest) {

        Response resp = cartService.removeProductFromCart(token, cartRequest);
        // Remove product from cart and get response

        return ResponseEntity.ok(resp);
        // return success response with status code 200 (OK)
    }

    @GetMapping("/get")
    public ResponseEntity<Response> getCart(@CookieValue("token") String token) {
        return ResponseEntity.ok(cartService.getCart(token));
    }
}
