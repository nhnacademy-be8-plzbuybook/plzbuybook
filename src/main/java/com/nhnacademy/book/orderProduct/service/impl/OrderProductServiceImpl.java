package com.nhnacademy.book.orderProduct.service.impl;

import com.nhnacademy.book.book.entity.SellingBook;
import com.nhnacademy.book.book.exception.SellingBookNotFoundException;
import com.nhnacademy.book.book.repository.SellingBookRepository;
import com.nhnacademy.book.deliveryFeePolicy.exception.NotFoundException;
import com.nhnacademy.book.order.dto.OrderProductStatusPatchRequestDto;
import com.nhnacademy.book.order.dto.orderRequests.OrderProductRequestDto;
import com.nhnacademy.book.order.entity.Orders;
import com.nhnacademy.book.order.service.OrderCacheService;
import com.nhnacademy.book.order.service.OrderProductCouponService;
import com.nhnacademy.book.orderProduct.entity.OrderProduct;
import com.nhnacademy.book.orderProduct.entity.OrderProductStatus;
import com.nhnacademy.book.orderProduct.repository.OrderProductRepository;
import com.nhnacademy.book.orderProduct.service.OrderProductService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class OrderProductServiceImpl implements OrderProductService {
    private final OrderProductRepository orderProductRepository;
    private final SellingBookRepository sellingBookRepository;
    private final OrderCacheService orderCacheService;
    private final OrderProductCouponService orderProductCouponService;

    @Transactional
    @Override
    public OrderProduct saveOrderProduct(Orders order, OrderProductRequestDto orderProductRequest) {
        SellingBook sellingBook = sellingBookRepository.findById(orderProductRequest.getProductId()).orElseThrow(() -> new SellingBookNotFoundException("찾을 수 없는 상품입니다."));
        // 판매책 재고 차감된 재고캐시를 rdb에 저장
        sellingBook.setSellingBookStock(orderCacheService.getProductStockCache(sellingBook.getSellingBookId()));

        BigDecimal couponDiscount = orderProductCouponService.calculateCouponDiscount(orderProductRequest);
        OrderProduct orderProduct = OrderProduct.builder()
                .sellingBook(sellingBook)
                .quantity(orderProductRequest.getQuantity())
                .price(orderProductRequest.getPrice())
                .status(OrderProductStatus.PAYMENT_COMPLETED)
                .couponDiscount(couponDiscount)
                .order(order)
                .build();
        return orderProductRepository.save(orderProduct);
    }

    @Transactional
    @Override
    public Optional<OrderProduct> findOrderProductBySellingBookId(Long sellingBookId) {
        return orderProductRepository.findBySellingBook_SellingBookId(sellingBookId);
    }

    @Transactional
    @Override
    public void patchStatus(Long orderProductId, OrderProductStatusPatchRequestDto patchRequest) {
        OrderProduct orderProduct = orderProductRepository.findById(orderProductId).orElseThrow(() -> new NotFoundException("주문상품을 찾을 수 없습니다. 주문상품 아이디: " + orderProductId));
        orderProduct.updateStatus(patchRequest.getStatus());
    }

    @Transactional
    @Override
    public void purchaseConfirmOrderProduct(Long orderProductId) {
        patchStatus(orderProductId, new OrderProductStatusPatchRequestDto(OrderProductStatus.PURCHASE_CONFIRMED));
    }

    @Transactional
    @Override
    public void addOrderProductStock(Long orderProductId, int quantity) {
        OrderProduct orderProduct = orderProductRepository.findById(orderProductId).orElseThrow(() -> new NotFoundException("주문상품을 찾을 수 없습니다."));
        SellingBook sellingBook = orderProduct.getSellingBook();
        sellingBook.setSellingBookStock(sellingBook.getSellingBookStock() + quantity);
    }
}
