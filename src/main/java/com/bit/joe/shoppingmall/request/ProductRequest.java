package com.bit.joe.shoppingmall.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductRequest {

    private Long categoryId;
    private String image;
    private String name;
    private int quantity;
    private int price;
}
