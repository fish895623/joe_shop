package com.bit.joe.shoppingmall.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import com.bit.joe.shoppingmall.dto.CartDto;
import com.bit.joe.shoppingmall.dto.CartItemDto;
import com.bit.joe.shoppingmall.dto.CategoryDto;
import com.bit.joe.shoppingmall.dto.OrderDto;
import com.bit.joe.shoppingmall.dto.OrderItemDto;
import com.bit.joe.shoppingmall.dto.ProductDto;
import com.bit.joe.shoppingmall.dto.UserDto;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Response {

    private int status;
    private String message;
    private final LocalDateTime timestamp = LocalDateTime.now();
    private String role;

    private CategoryDto category;
    private List<CategoryDto> categoryList;

    private ProductDto product;
    private List<ProductDto> productList;

    private UserDto user;
    private List<UserDto> userList;

    private OrderItemDto orderItem;
    private List<OrderItemDto> orderItemList;

    private OrderDto order;
    private List<OrderDto> orderList;

    private CartDto cart;
    private List<CartDto> cartList;

    private CartItemDto cartItem;
    private List<CartItemDto> cartItemList;

    private int totalPages;
    private long totalElements;
}
