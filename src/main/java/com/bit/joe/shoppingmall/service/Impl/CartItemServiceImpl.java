package com.bit.joe.shoppingmall.service.Impl;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;

import com.bit.joe.shoppingmall.dto.request.CartItemRequest;
import com.bit.joe.shoppingmall.dto.response.Response;
import com.bit.joe.shoppingmall.entity.Cart;
import com.bit.joe.shoppingmall.entity.CartItem;
import com.bit.joe.shoppingmall.entity.Product;
import com.bit.joe.shoppingmall.entity.User;
import com.bit.joe.shoppingmall.exception.NotFoundException;
import com.bit.joe.shoppingmall.mapper.CartItemMapper;
import com.bit.joe.shoppingmall.repository.CartItemRepository;
import com.bit.joe.shoppingmall.repository.CartRepository;
import com.bit.joe.shoppingmall.repository.ProductRepository;
import com.bit.joe.shoppingmall.repository.UserRepository;
import com.bit.joe.shoppingmall.service.CartItemService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartItemServiceImpl implements CartItemService {

    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final CartRepository cartRepository;

    /*TODO consider do we need method to add cart item, almost same function in the Cart Service*/
    @Override
    public Response addCartItem(CartItemRequest cartItemRequest) {

        Cart cart =
                cartRepository
                        .findById(cartItemRequest.getCartId())
                        .orElseThrow(() -> new NotFoundException("Cart not found"));
        // Get cart

        Product product =
                productRepository
                        .findById(cartItemRequest.getProductId())
                        .orElseThrow(() -> new NotFoundException("Product not found"));
        // Get product

        boolean isProductExists =
                cartItemRepository.findByCartAndProduct(cart, product).isPresent();

        // Check if product exists in cart
        if (isProductExists) {

            cartItemRequest.setQuantity(
                    cartItemRequest.getQuantity()
                            + cartItemRepository
                                    .findByCartAndProduct(cart, product)
                                    .orElseThrow(() -> new NotFoundException("Cart item not found"))
                                    .getQuantity());
            // update quantity

            return updateCartItem(cartItemRequest);
            // update cart item and return response
        }

        CartItem cartItem =
                CartItem.builder()
                        .cart(cart)
                        .product(product)
                        .quantity(cartItemRequest.getQuantity())
                        .price(
                                BigDecimal.valueOf(
                                        (long) cartItemRequest.getQuantity() * product.getPrice()))
                        .build();
        // Create cart item

        cartItemRepository.save(cartItem);
        // add product to cart item

        return Response.builder().status(200).message("Save cart item successfully").build();
    }

    @Override
    public Response removeCartItem(CartItemRequest cartItemRequest) {

        Cart cart =
                cartRepository
                        .findById(cartItemRequest.getCartId())
                        .orElseThrow(() -> new NotFoundException("Cart not found"));
        // Get cart

        Product product =
                productRepository
                        .findById(cartItemRequest.getProductId())
                        .orElseThrow(() -> new NotFoundException("Product not found"));
        // Get product

        cartItemRepository
                .findByCartAndProduct(cart, product)
                .ifPresent(cartItemRepository::delete);
        // Remove cart item if exists

        return Response.builder().status(200).message("Remove cart item successfully").build();
    }

    @Override
    public Response updateCartItem(CartItemRequest cartItemRequest) {

        Cart cart =
                cartRepository
                        .findById(cartItemRequest.getCartId())
                        .orElseThrow(() -> new NotFoundException("Cart not found"));
        // Get cart by user: with checking user exists and cart exists

        Product product =
                productRepository
                        .findById(cartItemRequest.getProductId())
                        .orElseThrow(() -> new NotFoundException("Product not found"));

        CartItem cartItem =
                cartItemRepository
                        .findByCartAndProduct(cart, product)
                        .orElseThrow(() -> new NotFoundException("Cart item not found"));
        // Get cart item by cart and product: with checking cart exists and product exists

        cartItem.setQuantity(cartItemRequest.getQuantity());
        cartItem.setPrice(
                BigDecimal.valueOf((long) cartItemRequest.getQuantity() * product.getPrice()));
        // set quantity and price

        cartItemRepository.save(cartItem);
        // save cart item

        return Response.builder().status(200).message("Update cart item successfully").build();
    }

    @Override
    public Response getCartItems(CartItemRequest cartItemRequest) {

        User user =
                userRepository
                        .findById(cartItemRequest.getUserId())
                        .orElseThrow(() -> new NotFoundException("User not found"));

        Cart cart =
                cartRepository
                        .findCartByUser(user)
                        .orElseThrow(() -> new NotFoundException("Cart not found"));

        List<CartItem> cartItems = cartItemRepository.findByCart(cart);
        // Get cart items by cart

        return Response.builder()
                .status(200)
                .cartItemList(cartItems.stream().map(CartItemMapper::toDto).toList())
                .message("Cart items successfully")
                .build();
    }

    @Override
    public Response getCartItem(CartItemRequest cartItemRequest) {

        User user =
                userRepository
                        .findById(cartItemRequest.getUserId())
                        .orElseThrow(() -> new NotFoundException("User not found"));
        // Get user

        Cart cart =
                cartRepository
                        .findCartByUser(user)
                        .orElseThrow(() -> new NotFoundException("Cart not found"));
        // Get cart

        Product product =
                productRepository
                        .findById(cartItemRequest.getProductId())
                        .orElseThrow(() -> new NotFoundException("Product not found"));
        // Get product

        CartItem cartItem =
                cartItemRepository
                        .findByCartAndProduct(cart, product)
                        .orElseThrow(() -> new NotFoundException("Cart item not found"));
        // Get cart item

        return Response.builder()
                .status(200)
                .cartItem(CartItemMapper.toDto(cartItem))
                .message("Cart item successfully")
                .build();
        // return success response
    }

    @Override
    public Response getAllCartItems() {

        List<CartItem> cartItems = cartItemRepository.findAll();
        // Get all cart items

        return Response.builder()
                .status(200)
                .cartItemList(cartItems.stream().map(CartItemMapper::toDto).toList())
                .message("All cart items successfully")
                .build();
        // return success response
    }

    // TODO consider to put this method in CartService
    @Override
    public Response clearCart(CartItemRequest cartItemRequest) {

        User user =
                userRepository
                        .findById(cartItemRequest.getUserId())
                        .orElseThrow(() -> new NotFoundException("User not found"));
        // Get user

        Cart cart =
                cartRepository
                        .findCartByUser(user)
                        .orElseThrow(() -> new NotFoundException("Cart not found"));
        // Get cart

        cart.getCartItems().clear();
        // Clear cart items

        cartRepository.save(cart);
        // Save cart

        return Response.builder().status(200).message("Clear cart successfully").build();
        // return success response
    }
}
