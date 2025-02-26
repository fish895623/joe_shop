package com.bit.joe.shoppingmall.service;

import com.bit.joe.shoppingmall.dto.CartDto;
import com.bit.joe.shoppingmall.dto.Response;

public interface CartService {
  public Response createCart(CartDto cartDto, Long userId);

  public Response appendProductToCart(Long userId, Long productId, int quantity);

  public Response removeProductFromCart(Long userId, Long productId);
}
