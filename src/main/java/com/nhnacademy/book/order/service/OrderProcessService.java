package com.nhnacademy.book.order.service;

import com.nhnacademy.book.order.dto.OrderCancelRequestDto;
import com.nhnacademy.book.order.dto.orderRequests.OrderRequestDto;
import com.nhnacademy.book.order.dto.orderResponse.OrderResponseDto;

import java.math.BigDecimal;

public interface OrderProcessService {
    OrderResponseDto processRequestedOrder(OrderRequestDto orderRequest);
    String completeOrder(String orderId);
    void cancelOrder(String orderId, OrderCancelRequestDto cancelRequest);

}
