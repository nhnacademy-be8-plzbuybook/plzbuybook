package com.nhnacademy.book.order.dto;

import com.nhnacademy.book.order.enums.OrderStatus;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
public class OrderDto {
    // 주문일
    private LocalDate orderDate;
    // 주문상태
    private OrderStatus orderStatus;
    // 주문 상품 썸네일
//    private String thumbNail;
    // 주문명
    private String orderName;
    // 결제금액
    private BigDecimal paymentAmount;

    @QueryProjection
    public OrderDto(LocalDateTime orderedAt, OrderStatus orderStatus, String orderName, BigDecimal paymentAmount) {
        this.orderDate = orderedAt.toLocalDate();
        this.orderStatus = orderStatus;
        this.orderName = orderName;
        this.paymentAmount = paymentAmount;
    }
}
