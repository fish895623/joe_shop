package com.bit.joe.shoppingmall.entity;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "cart")
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<CartItem> cartItems;

    private LocalDateTime createdAt;

    private Boolean isOrdered;

    // JPA 엔티티가 DB에 저장되기 전에 실행
    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
        isOrdered = false;
    }

    /**
     * Append a cart item to the cart
     *
     * @param cartItem
     */
    public void appendCartItemToCart(CartItem cartItem) {
        cartItem.setCart(this);
        cartItems.add(cartItem);
    }
}
