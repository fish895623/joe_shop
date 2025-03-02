package com.bit.joe.shoppingmall.dto.request;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class CartItemRequest {

    private Long userId;
    private Long productId;
    private Long cartId;
    private int quantity;
}
