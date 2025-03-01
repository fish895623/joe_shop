package com.bit.joe.shoppingmall.service;

import java.math.BigDecimal;

import com.bit.joe.shoppingmall.dto.request.CartItemRequest;
import com.bit.joe.shoppingmall.dto.response.Response;

public interface CartItemService {
    Response addCartItem(CartItemRequest cartItemRequest);

    Response removeCartItem(long userId, long productId);

    Response updateCartItem(long userId, long productId, int quantity, BigDecimal price);

    Response getCartItems(long userId);

    Response getCartItem(long userId, long productId);

    // TODO consider to put this method in CartService
    Response clearCart(long userId);
}
