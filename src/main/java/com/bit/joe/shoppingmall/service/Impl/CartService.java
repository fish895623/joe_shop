package com.bit.joe.shoppingmall.service.Impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.bit.joe.shoppingmall.dto.UserDto;
import com.bit.joe.shoppingmall.dto.request.CartRequest;
import com.bit.joe.shoppingmall.dto.response.Response;
import com.bit.joe.shoppingmall.entity.Cart;
import com.bit.joe.shoppingmall.entity.CartItem;
import com.bit.joe.shoppingmall.entity.Product;
import com.bit.joe.shoppingmall.entity.User;
import com.bit.joe.shoppingmall.exception.NotFoundException;
import com.bit.joe.shoppingmall.jwt.JWTUtil;
import com.bit.joe.shoppingmall.mapper.CartMapper;
import com.bit.joe.shoppingmall.mapper.UserMapper;
import com.bit.joe.shoppingmall.repository.CartItemRepository;
import com.bit.joe.shoppingmall.repository.CartRepository;
import com.bit.joe.shoppingmall.repository.ProductRepository;
import com.bit.joe.shoppingmall.repository.UserRepository;
import com.bit.joe.shoppingmall.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Service
@Slf4j
public class CartService {
    private final ProductRepository productRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final JWTUtil jwtUtil;

    /**
     * Create a cart initially (when user registers)
     *
     * @return Response
     */
    public Response createCart(CartRequest cartRequest) {

        User user =
                userRepository
                        .findById(cartRequest.getUserId())
                        .orElseThrow(() -> new NotFoundException("User not found"));
        // get user from the context holder (logged-in user)
        Cart cart = new Cart();
        // create a new cart (empty)

        cart.setUser(user);
        // set the user of the cart

        cart.setCartItems(List.<CartItem>of());
        // set empty cart items

        var createdCart = cartRepository.save(cart);
        // save the cart to the database

        return Response.builder()
                .status(200)
                .message("Cart created successfully")
                .cart(CartMapper.toDto(createdCart))
                .build();
        // return success response with status code 200 -> cart created successfully
    }

    /**
     * Append a product to the cart
     *
     * @param cartRequest CartRequest
     * @return Response
     */
    public Response appendProductToCart(String token, CartRequest cartRequest) {
        // Create Cart if there is no not ordered cart, otherwise use the existing one

        Long productIdToAppend = cartRequest.getProductId();
        int quantityToAppend = cartRequest.getQuantity();
        // get parms from the request

        String loggedInUserEmail = jwtUtil.getUsername(token);
        // get user from the context holder (logged-in user)

        UserDto loggedInUserDto = userService.getUserByEmail(loggedInUserEmail).getUser();
        User loggedInUserEntity = UserMapper.toEntity(loggedInUserDto);

        Cart cart =
                cartRepository
                        .findCartByUser(loggedInUserEntity)
                        .orElseGet(
                                () -> {
                                    Cart newCart = new Cart();
                                    newCart.setUser(loggedInUserEntity);
                                    newCart.setCartItems(new ArrayList<>(List.of()));
                                    return newCart;
                                });
        // find cart by user (found by user) or create a new cart if not found

        // if cart items is null, set it to an empty list
        if (cart.getCartItems() == null) {
            cart.setCartItems(new ArrayList<>(List.of()));
        }

        for (CartItem cartItem : cart.getCartItems()) {
            if (cartItem.getProduct().getId().equals(productIdToAppend)) {
                cartItem.setQuantity(cartItem.getQuantity() + quantityToAppend);
                cartItem.setTotalPrice();
                cartItemRepository.save(cartItem);
                return Response.builder()
                        .status(200)
                        .message("Product quantity updated successfully(append product to cart)")
                        .build();
            }
        }
        // if same product already exists in the cart, update only quantity

        Product product =
                productRepository
                        .findById(productIdToAppend)
                        .orElseThrow(() -> new NotFoundException("Product not found"));
        // Find product by id

        CartItem cartItem =
                CartItem.builder().cart(cart).product(product).quantity(quantityToAppend).build();
        cartItem.setTotalPrice();
        // Create a new cart item with the found product, quantity and price

        // save the cart item
        cartItem = cartItemRepository.save(cartItem);

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

    /**
     * Remove a product from the cart
     *
     * @param cartRequest CartRequest
     * @return Response
     */
    public Response removeProductFromCart(CartRequest cartRequest) {

        Long productIdToRemove = cartRequest.getProductId();
        int quantityToRemove = cartRequest.getQuantity();

        User user = userService.getLoginUser();
        // get user from the context holder (logged-in user)

        Cart cart =
                cartRepository
                        .findCartByUser(user)
                        .orElseThrow(() -> new NotFoundException("Cart not found"));
        // find cart by user (found by id)

        // remove product from cart if it exists
        for (CartItem cartItem : cart.getCartItems()) {
            if (cartItem.getProduct().getId().equals(productIdToRemove)) {

                // check if cart item quantity is greater than the quantity to remove
                if (cartItem.getQuantity() > quantityToRemove) {
                    cartItem.setQuantity(cartItem.getQuantity() - quantityToRemove);
                    cartItem.setTotalPrice();
                    cartItemRepository.save(cartItem);
                    return Response.builder()
                            .status(200)
                            .message(
                                    "Product quantity updated successfully(remove product from cart)")
                            .build();
                }

                // remove the cart item from the cart
                cart.getCartItems().remove(cartItem);
                cartRepository.save(cart);
                return Response.builder()
                        .status(200)
                        .message("Product removed from cart successfully")
                        .build();
            }
        }

        return Response.builder()
                .status(404)
                .message("Product to be removed not found in cart")
                .build();
        // return not found response with status code 404 -> product not found in cart
    }

    /**
     * Get the cart
     *
     * @param token String
     * @return Response
     */
    public Response getCart(String token) {
        // get user email from the token
        String loggedInUserEmail = jwtUtil.getUsername(token);
        // get user from the context holder (logged-in user)
        UserDto userDto = userService.getUserByEmail(loggedInUserEmail).getUser();
        // Convert UserDto to User
        User user = UserMapper.toEntity(userDto);
        // Find cart by user
        Cart cart =
                cartRepository
                        .findCartByUser(user)
                        .orElseThrow(() -> new NotFoundException("Cart not found (in getCart method)"));

        // return success response with status code 200 -> cart retrieved successfully
        return Response.builder()
                .status(200)
                .message("Cart retrieved successfully")
                .cart(CartMapper.toDto(cart))
                .build();
    }
}
