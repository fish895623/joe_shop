package com.bit.joe.shoppingmall.dto.request;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderRequest {

    private Long userId;
    private List<Long> cartItemIds;
    private LocalDateTime orderDate;
}
