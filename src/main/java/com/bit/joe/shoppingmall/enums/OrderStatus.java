package com.bit.joe.shoppingmall.enums;

public enum OrderStatus {
    ORDER,
    CONFIRM,
    PREPARE,
    DELIVER,
    DELIVERED,
    RETURN_REQUESTED,
    RETURN_IN_PROGRESS,
    RETURNED, // need to convert to COMPLETE
    CANCEL_REQUESTED,
    CANCELLED, // need to convert to COMPLETE
    COMPLETE,
}
