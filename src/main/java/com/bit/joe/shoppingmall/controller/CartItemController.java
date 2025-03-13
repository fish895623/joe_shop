package com.bit.joe.shoppingmall.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.bit.joe.shoppingmall.dto.request.CartItemRequest;
import com.bit.joe.shoppingmall.dto.response.Response;
import com.bit.joe.shoppingmall.service.CartItemService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/cart-item")
@RequiredArgsConstructor
public class CartItemController {

    private final CartItemService cartItemService;

    // create cart item
    @PostMapping("/create")
    public ResponseEntity<Response> createCartItem(@RequestBody CartItemRequest cartItemRequest) {
        log.info("create cart item");

        return ResponseEntity.status(200).body(cartItemService.addCartItem(cartItemRequest));
    }

    // delete cart item
    @DeleteMapping("/delete")
    public ResponseEntity<Response> deleteCartItem(@RequestBody CartItemRequest cartItemRequest) {
        log.info("delete cart item");

        return ResponseEntity.status(200).body(cartItemService.removeCartItem(cartItemRequest));
    }

    // update cart item
    @PostMapping("/update")
    public ResponseEntity<Response> updateCartItem(@RequestBody CartItemRequest cartItemRequest) {
        log.info("update cart item");

        Response resp = cartItemService.updateCartItem(cartItemRequest);

        return ResponseEntity.status(200).body(resp);
    }

    // get cart item
    @GetMapping("/get")
    public ResponseEntity<Response> getCartItem(@RequestBody CartItemRequest cartItemRequest) {
        log.info("get cart item");

        return ResponseEntity.status(200).body(cartItemService.getCartItem(cartItemRequest));
    }

    @GetMapping("/get-all")
    public ResponseEntity<Response> getAllCartItems() {
        log.info("get all cart items");

        return ResponseEntity.status(200).body(cartItemService.getAllCartItems());
    }

    @GetMapping("/get-all-by-user")
    public ResponseEntity<Response> getCartItemsByUser(@RequestBody CartItemRequest cartItemRequest) {
        log.info("get all cart items by user id");

        return ResponseEntity.status(200).body(cartItemService.getCartItems(cartItemRequest));
    }

    @PostMapping("/clear")
    public ResponseEntity<Response> clearCart(@RequestBody CartItemRequest cartItemRequest) {
        log.info("clear cart by user id");

        return ResponseEntity.status(200).body(cartItemService.clearCart(cartItemRequest));
    }
}
