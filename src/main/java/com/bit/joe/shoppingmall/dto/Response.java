package com.bit.joe.shoppingmall.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Response {

    // "응답"시의 DTO
    private int status;
    private String message;
    private final LocalDateTime timestamp = LocalDateTime.now();
    private String role;

    private CategoryDto category;
    private List<CategoryDto> categoryList;

    private ProductDto product;
    private List<ProductDto> productList;

//    private UserDto user;
//    private List<UserDto> userList;

//    private OrderItemDto orderItem;
//    private List<OrderItemDto> orderItemList;

//    private OrderDto order;
//    private List<OrderDto> orderList;

}
