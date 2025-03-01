package com.bit.joe.shoppingmall.dto.request;

import lombok.Data;

@Data
public class CartItemRequest {

    private Long userId;
    private Long productId;
    private Long cartId;
    private int quantity;
}
