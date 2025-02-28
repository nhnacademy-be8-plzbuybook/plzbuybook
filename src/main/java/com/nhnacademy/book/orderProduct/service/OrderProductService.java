package com.nhnacademy.book.orderProduct.service;

import com.nhnacademy.book.order.dto.OrderProductStatusPatchRequestDto;
import com.nhnacademy.book.order.dto.orderRequests.OrderProductRequestDto;
import com.nhnacademy.book.order.entity.Orders;
import com.nhnacademy.book.orderProduct.entity.OrderProduct;

import java.util.Optional;

public interface OrderProductService {
    OrderProduct saveOrderProduct(Orders order, OrderProductRequestDto orderProductRequest);
    Optional<OrderProduct> findOrderProductBySellingBookId(Long sellingBookId);
    void patchStatus(Long orderProductId, OrderProductStatusPatchRequestDto patchRequest);
    void purchaseConfirmOrderProduct(Long orderProductId);
    void addOrderProductStock(Long orderProductId, int quantity);
}
