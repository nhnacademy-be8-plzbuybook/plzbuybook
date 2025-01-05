package com.nhnacademy.book.order.service.impl;

import com.nhnacademy.book.book.dto.response.BookDetailResponseDto;
import com.nhnacademy.book.book.service.Impl.SellingBookService;
import com.nhnacademy.book.order.dto.orderRequests.OrderProductRequestDto;
import com.nhnacademy.book.order.dto.orderRequests.OrderRequestDto;
import com.nhnacademy.book.order.dto.orderResponse.OrderResponseDto;
import com.nhnacademy.book.order.entity.Orders;
import com.nhnacademy.book.order.enums.OrderStatus;
import com.nhnacademy.book.order.repository.OrderRepository;
import com.nhnacademy.book.order.service.OrderCrudService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

/**
 * 주문 CRUD 서비스
 */
@RequiredArgsConstructor
@Service
public class OrderCrudServiceImpl implements OrderCrudService {
    private final OrderRepository orderRepository;
    private final SellingBookService sellingBookService;

    @Transactional
    @Override
    public OrderResponseDto createOrder(OrderRequestDto orderRequest) {
        // 주문 생성
        LocalDateTime currentTime = LocalDateTime.now();
        Orders order = Orders.builder()
                .id(generateOrderId())
                .number(generateOrderNumber(currentTime))
                .name(generateOrderName(orderRequest))
                .deliveryFee(orderRequest.getDeliveryFee())
                .usedPoint(orderRequest.getUsedPoint())
                .deliveryWishDate(orderRequest.getDeliveryWishDate())
                .orderedAt(currentTime)
                .orderPrice(orderRequest.getOrderPrice())
                .status(OrderStatus.PAYMENT_PENDING)
                .build();

        // 주문 저장
        Orders savedOrder = orderRepository.save(order);
        BigDecimal paymentPrice = orderRequest.getOrderPrice().add(orderRequest.getDeliveryFee());
        return new OrderResponseDto(savedOrder.getId(), paymentPrice, savedOrder.getName());
    }


    /**
     * 주문이름 생성
     *
     * @param order 주문 요청
     * @return 생성된 주문이름
     */
    private String generateOrderName(OrderRequestDto order) {
        List<OrderProductRequestDto> orderProducts = order.getOrderProducts();
        BookDetailResponseDto book = sellingBookService.getSellingBook(orderProducts.getFirst().getProductId());
        if (orderProducts.size() > 1) {
            return String.format("%s 외 %d 건", book.getBookTitle(), orderProducts.size());
        }
        return book.getBookTitle();
    }


    /**
     * 주문번호 생성
     *
     * @param orderedAt 주문일시
     * @return 생성된 주문번호
     */
    private String generateOrderNumber(LocalDateTime orderedAt) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyMMdd-HHmmssSSSS");
        return orderedAt.format(formatter);
    }


    /**
     * UUID를 기반으로 주문 ID 생성
     *
     * @return 생성된 주문 ID
     */
    private String generateOrderId() {
        return UUID.randomUUID().toString();
    }
}
