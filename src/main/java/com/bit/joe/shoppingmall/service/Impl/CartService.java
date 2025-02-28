package com.bit.joe.shoppingmall.service.Impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.bit.joe.shoppingmall.dto.response.Response;
import com.bit.joe.shoppingmall.entity.Cart;
import com.bit.joe.shoppingmall.entity.CartItem;
import com.bit.joe.shoppingmall.entity.Product;
import com.bit.joe.shoppingmall.entity.User;
import com.bit.joe.shoppingmall.exception.NotFoundException;
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

    // Create a cart initially (when user registers)
    public Response createCart(Long userId) {
        User user =
                userRepository
                        .findById(userId)
                        .orElseThrow(() -> new NotFoundException("User not found"));
        Cart cart = new Cart();
        // Create a new cart (empty)
        cart.setUser(user);
        // Set the user of the cart

        cartRepository.save(cart);

        return Response.builder().status(200).message("Cart created successfully").build();
    }

    /** {@summary} Append a product to the cart */
    public Response appendProductToCart(Long userId, Long productId, int quantity) {
        // Create Cart if there is no not ordered cart, otherwise use the existing one
        User user =
                userRepository
                        .findById(userId)
                        .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // find cart by user
        Cart cart = cartRepository.findCartByUser(user).get();

        // Find product by id
        Product product =
                productRepository
                        .findById(productId)
                        .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        CartItem cartItem =
                CartItem.builder().cart(cart).product(product).quantity(quantity).build();

        List<CartItem> cartItems = cart.getCartItems();
        cartItems.add(cartItem);

        cart.setCartItems(cartItems);

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
