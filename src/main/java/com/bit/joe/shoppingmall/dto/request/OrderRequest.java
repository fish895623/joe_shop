package com.bit.joe.shoppingmall.dto.request;

import java.time.LocalDateTime;
import java.util.List;

import com.bit.joe.shoppingmall.enums.OrderStatus;
import com.bit.joe.shoppingmall.enums.RequestType;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderRequest {

    private Long orderId;
    private Long userId;
    private List<Long> cartItemIds;
    private LocalDateTime orderDate;
    private OrderStatus status;
    private RequestType requestType;
}
