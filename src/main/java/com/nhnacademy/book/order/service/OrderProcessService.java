package com.nhnacademy.book.order.service;

import com.nhnacademy.book.order.dto.orderRequests.OrderRequestDto;
import com.nhnacademy.book.order.dto.orderResponse.OrderResponseDto;

public interface OrderProcessService {
    OrderResponseDto requestOrder(OrderRequestDto orderRequest);
    String completeOrder(String orderId);
}
