package com.bit.joe.shoppingmall.service.Impl;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;

import com.bit.joe.shoppingmall.dto.response.Response;
import com.bit.joe.shoppingmall.entity.Cart;
import com.bit.joe.shoppingmall.entity.CartItem;
import com.bit.joe.shoppingmall.entity.Product;
import com.bit.joe.shoppingmall.entity.User;
import com.bit.joe.shoppingmall.exception.NotFoundException;
import com.bit.joe.shoppingmall.repository.*;
import com.bit.joe.shoppingmall.service.UserService;

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
    private final UserService userService;

    // --------------- createCart ---------------
    /** {@summary} Create a cart initially (when user registers) */
    public Response createCart(Long userId) {
        //        User user =
        //                userRepository
        //                        .findById(userId)
        //                        .orElseThrow(() -> new NotFoundException("User not found"));
        User user = userService.getLoginUser();
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

        // find cart by user, if cart not found, create a new cart
        Cart cart = cartRepository.findCartByUser(user).orElseGet(Cart::new);

        // if cart is created now, set user
        if (cart.getUser() == null) {
            cart.setUser(user);
            // set user to the cart
        }

        // if cart is already created, check if the product already exists in the cart
        if (cart.getCartItems() != null) {

            // if same product already exists in the cart, update only quantity
            for (CartItem cartItem : cart.getCartItems()) {
                if (cartItem.getProduct().getId().equals(productId)) {
                    cartItem.setQuantity(cartItem.getQuantity() + quantity);
                    cartItem.setPrice(
                            BigDecimal.valueOf(
                                    (long) cartItem.getQuantity()
                                            * cartItem.getProduct().getPrice()));
                    cartItemRepository.save(cartItem);
                    return Response.builder()
                            .status(200)
                            .message("Product quantity updated successfully")
                            .build();
                }
            }
        } else {

            Product product =
                    productRepository
                            .findById(productId)
                            .orElseThrow(() -> new NotFoundException("Product not found"));
            // Find product by id

            CartItem cartItem =
                    CartItem.builder()
                            .cart(cart)
                            .product(product)
                            .quantity(quantity)
                            .price(BigDecimal.valueOf((long) quantity * product.getPrice()))
                            .build();
            // Create a new cart item with the found product, quantity and price

            cart.setCartItems(List.of(cartItem));
            // Add the cart item to the cart
        }

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
