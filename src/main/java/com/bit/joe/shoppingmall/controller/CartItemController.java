package com.bit.joe.shoppingmall.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.bit.joe.shoppingmall.dto.request.CartItemRequest;
import com.bit.joe.shoppingmall.dto.response.Response;
import com.bit.joe.shoppingmall.service.CartItemService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
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
    @GetMapping("/delete")
    public ResponseEntity<Response> deleteCartItem(@RequestBody CartItemRequest cartItemRequest) {
        log.info("delete cart item");

        return ResponseEntity.status(200).body(cartItemService.removeCartItem(cartItemRequest));
    }

    // update cart item
    @PostMapping("/update")
    public ResponseEntity<Response> updateCartItem(@RequestBody CartItemRequest cartItemRequest) {
        log.info("update cart item");

        return ResponseEntity.status(200).body(cartItemService.updateCartItem(cartItemRequest));
    }

    // get cart item
    @GetMapping("/get")
    public ResponseEntity<Response> getCartItem(@RequestBody CartItemRequest cartItemRequest) {
        log.info("get cart item");

        return ResponseEntity.status(200).body(cartItemService.getCartItem(cartItemRequest));
    }

    // get all cart items
    @GetMapping("/get-all")
    public ResponseEntity<Response> getAllCartItems() {
        log.info("get all cart items");

        return ResponseEntity.status(200).body(cartItemService.getAllCartItems());
    }

    // get all cart items by user id
    @GetMapping("/get-all/{userId}")
    public ResponseEntity<Response> getAllCartItemsByUserId(@PathVariable Long userId) {
        log.info("get all cart items by user id");

        return ResponseEntity.status(200)
                .body(cartItemService.getCartItems(CartItemRequest.builder().userId(userId).build()));
    }
}
