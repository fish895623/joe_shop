package com.bit.joe.shoppingmall.service.Impl;

import org.springframework.stereotype.Service;

import com.bit.joe.shoppingmall.dto.CartDto;
import com.bit.joe.shoppingmall.dto.Response;
import com.bit.joe.shoppingmall.repository.CartRepository;
import com.bit.joe.shoppingmall.repository.OrderRepository;
import com.bit.joe.shoppingmall.repository.ProductRepository;
import com.bit.joe.shoppingmall.repository.UserRepository;
import com.bit.joe.shoppingmall.service.CartService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartServiceImpl implements CartService {
    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    public Response createCart(CartDto cartDto, Long userId) {
        return null;
    }

    public Response appendProductToCart(Long userId, Long productId, int quantity) {
        // Implementation here
        return null;
    }

    public Response removeProductFromCart(Long userId, Long productId) {
        // Implementation here
        return null;
    }
}
