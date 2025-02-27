package com.bit.joe.shoppingmall.service.Impl;

import org.springframework.stereotype.Service;

import com.bit.joe.shoppingmall.dto.response.Response;
import com.bit.joe.shoppingmall.entity.Cart;
import com.bit.joe.shoppingmall.entity.CartItem;
import com.bit.joe.shoppingmall.entity.Product;
import com.bit.joe.shoppingmall.entity.User;
import com.bit.joe.shoppingmall.repository.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Service
@Slf4j
public class CartService {
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;

    public Response createCart(Long userId) {
        User user =
                userRepository
                        .findById(userId)
                        .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Cart cart = Cart.builder().user(user).build();
        cartRepository.save(cart);
        return Response.builder().status(200).message("Cart created successfully").build();
    }

    public Response appendProductToCart(Long cartId, Long productId, int quantity) {
        Cart cart =
                cartRepository
                        .findById(cartId)
                        .orElseThrow(() -> new IllegalArgumentException("Cart not found"));

        log.info("{} {}", cart.getCreatedAt(), cart.getUser().getName());

        Product product =
                productRepository
                        .findById(productId)
                        .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        CartItem cartItem =
                CartItem.builder().cart(cart).quantity(quantity).product(product).build();
        cartItemRepository.save(cartItem);
        cartItem.setId(cartItem.getId());

        log.info("{} {}", cartItem.getId(), cartItem.getProduct().getName());

        cart.getCartItems().add(cartItem);

        cartRepository.save(cart);

        return Response.builder()
                .status(200)
                .message("Product appended to cart successfully")
                .build();
    }

    public Response removeProductFromCart(Long userId, Long productId) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
