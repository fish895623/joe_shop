package com.bit.joe.shoppingmall.service.Impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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

    // --------------- createCart ---------------
    /** {@summary} Create a cart initially (when user registers) */
    public Response createCart(Long userId) {
        User user =
                userRepository
                        .findById(userId)
                        .orElseThrow(() -> new NotFoundException("User not found"));
        Cart cart = new Cart();
        // Create a new cart (empty)
        cart.setUser(user);
        // Set the user of the cart

        cartRepository.save(cart); // db로 저장

        return Response.builder().status(200).message("Cart created successfully").build();
    }

    // --------------- appendProductToCart ---------------
    /** {@summary} Append a product to the cart */
    public Response appendProductToCart(Long userId, Long productId, int quantity) {
        // Create Cart if there is no not ordered cart, otherwise use the existing one
        User user =
                userRepository
                        .findById(userId)
                        .orElseThrow(() -> new NotFoundException("User not found"));

        Cart cart =
                cartRepository
                        .findCartByUser(user)
                        .orElseGet(
                                () -> {
                                    // If no cart is found, create a new one
                                    Cart newCart = new Cart();
                                    newCart.setUser(user);
                                    newCart.setCreatedAt(LocalDateTime.now());
                                    newCart.setIsOrdered(false);
                                    cartRepository.save(
                                            newCart); // Save the new cart to the repository
                                    return newCart;
                                });

        // if same product already exists in the cart, update only quantity
        for (CartItem cartItem : cart.getCartItems()) {
            // productId in cart items -> equals to productId passed as a parameter?
            if (cartItem.getProduct().getId().equals(productId)) {
                cartItem.setQuantity(cartItem.getQuantity() + quantity); // add one more
                cartItem.setPrice(
                        BigDecimal.valueOf(
                                (long) cartItem.getQuantity() * cartItem.getProduct().getPrice()));
                cartItemRepository.save(cartItem);
                return Response.builder()
                        .status(200)
                        .message("Product quantity updated successfully")
                        .build();
            }
        }

        // Find product by id
        Product product =
                productRepository
                        .findById(productId)
                        .orElseThrow(() -> new NotFoundException("Product not found"));

        CartItem cartItem =
                CartItem.builder()
                        .cart(cart)
                        .product(product)
                        .quantity(quantity)
                        .price(BigDecimal.valueOf((long) quantity * product.getPrice()))
                        .build();
        // Create a new cart item with the found product, quantity and price

        cart.getCartItems().add(cartItem);
        // Add the cart item to the cart

        cartRepository.save(cart);
        // Save the cart

        return Response.builder()
                .status(200)
                .message("Product appended to cart successfully")
                .build();
        // return success response with status code 200 -> product appended to cart successfully
    }

    // --------------- removeProductFromCart ---------------
    /** {@summary} Remove a product from the cart */
    public Response removeProductFromCart(Long userId, Long productId) {

        Cart cart =
                cartRepository
                        .findCartByUser(
                                userRepository
                                        .findById(userId)
                                        .orElseThrow(() -> new NotFoundException("User not found")))
                        .orElseThrow(() -> new NotFoundException("Cart not found"));
        // find cart by user (found by id)

        // remove product from cart if it exists
        for (CartItem cartItem : cart.getCartItems()) {
            if (cartItem.getProduct().getId().equals(productId)) {
                cart.getCartItems().remove(cartItem);
                cartRepository.save(cart);
                return Response.builder()
                        .status(200)
                        .message("Product removed from cart successfully")
                        .build();
            }
        }

        return Response.builder().status(404).message("Product not found in cart").build();
        // return not found response with status code 404 -> product not found in cart
    }
}
