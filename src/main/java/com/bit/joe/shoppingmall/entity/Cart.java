package com.bit.joe.shoppingmall.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "cart")
@Data
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;  // Long 타입의 user_id 참조

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;  // Long 타입의 product_id 참조

    @Column(name = "quantity", nullable = false)
    private int quantity;
}