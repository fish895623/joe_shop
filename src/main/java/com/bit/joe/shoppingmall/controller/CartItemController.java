package com.bit.joe.shoppingmall.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
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

    // update cart item

    // get cart item
}
