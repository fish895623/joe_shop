package com.bit.joe.shoppingmall.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bit.joe.shoppingmall.entity.Order;
import com.bit.joe.shoppingmall.entity.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    /**
     * Find all order items by order
     *
     * @param order 찾고자 하는 주문 아이템을 가지고 있는 주문
     * @return List<OrderItem>
     */
    List<OrderItem> findByOrder(Order order);
}
