package com.bit.joe.shoppingmall.dto.request;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductRequest {
    private Long productId;

    @NotNull(message = "Category ID is required")
    private Long categoryId;

    private String image;

    @NotEmpty(message = "Name is required")
    private String name;

    @Min(value = 1, message = "Quantity must be greater than 0")
    private int quantity;

    // TODO: Need to change to float? -> fine for Korean Won, also can be fixed later
    @Min(value = 1, message = "Price must be greater than 0")
    private int price;
}
