package com.bit.joe.shoppingmall.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bit.joe.shoppingmall.entity.Cart;
import com.bit.joe.shoppingmall.entity.CartItem;
import com.bit.joe.shoppingmall.entity.Product;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    List<CartItem> findByProduct(Product product);

    Optional<CartItem> findByCartAndProduct(Cart cart, Product product);

    List<CartItem> findByCart(Cart cart);
}
