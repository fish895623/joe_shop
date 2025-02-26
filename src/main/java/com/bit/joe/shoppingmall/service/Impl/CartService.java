package com.bit.joe.shoppingmall.service.Impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.bit.joe.shoppingmall.dto.Response;
import com.bit.joe.shoppingmall.entity.Cart;
import com.bit.joe.shoppingmall.entity.CartItem;
import com.bit.joe.shoppingmall.entity.Product;
import com.bit.joe.shoppingmall.entity.User;
import com.bit.joe.shoppingmall.repository.CartRepository;
import com.bit.joe.shoppingmall.repository.OrderRepository;
import com.bit.joe.shoppingmall.repository.ProductRepository;
import com.bit.joe.shoppingmall.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartService {
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;

    public Response createCart(Long userId) {
        User user =
                userRepository
                        .findById(userId)
                        .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Cart cart = Cart.builder().user(user).build();
        cartRepository.save(cart);
        return Response.builder().status(200).message("Cart created successfully").build();
    }

    @Transactional
    public Response appendProductToCart(Long cartId, Long userId, Long productId, int quantity) {

        // Find user by userId
        User user =
                userRepository
                        .findById(userId)
                        .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Find cart by cartId or create new cart if not found
        Cart cart =
                cartRepository
                        .findById(cartId)
                        .orElseGet(
                                () -> {
                                    log.info("Cart not found, creating new cart");
                                    return Cart.builder().id(cartId).user(user).build();
                                });

        // Find product by productId
        Product product =
                productRepository
                        .findById(productId)
                        .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        // Put product into CartItem
        CartItem cartItem = CartItem.builder().product(product).quantity(quantity).build();

        // Add cartItem to cart
        List<CartItem> cartItems = cart.getCartItems();
        if (cartItems == null) {
            cartItems = new ArrayList<>();
        }
        cartItems.add(cartItem);
        cart.setCartItems(cartItems);

        // Save cart
        cartRepository.save(cart);

        log.info("{}", cartItem.toString());

        return Response.builder()
                .status(200)
                .message("Product appended to cart successfully")
                .build();
    }

    public Response removeProductFromCart(Long userId, Long productId) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
