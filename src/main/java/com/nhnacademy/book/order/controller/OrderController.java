package com.nhnacademy.book.order.controller;

import com.nhnacademy.book.member.domain.service.MemberService;
import com.nhnacademy.book.order.dto.*;
import com.nhnacademy.book.order.service.OrderService;
import com.nhnacademy.book.orderProduct.service.OrderProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class OrderController {
    private final OrderService orderService;
    private final MemberService memberService;
    private final OrderProductService orderProductService;
    /**
     * 주문 목록조회(관리자)
     *
     * @param searchRequest 주문조회 DTO
     * @param pageable      페이지 정보
     * @return 주문목록 DTO
     */
    @GetMapping("/api/orders")
    public ResponseEntity<Page<OrderDto>> getAllOrders(@ModelAttribute OrderSearchRequestDto searchRequest,
                                                       Pageable pageable) {
        Page<OrderDto> orders = orderService.getOrders(searchRequest, pageable);

        return ResponseEntity.ok(orders);
    }


    /**
     * 내 주문내역 조회
     *
     * @param memberEmail 회원 이메일
     * @param searchRequest 검색 파라미터
     * @param pageable 페이징
     * @return 주문목록 페이지
     */
    @GetMapping("/api/orders/my")
    public ResponseEntity<Page<OrderDto>> getMyOrders(@RequestHeader("X-USER-ID") String memberEmail,
                                                      @RequestParam(required = false) OrderSearchRequestDto searchRequest,
                                                      Pageable pageable) {
        // 회원 검증
        if (memberEmail == null || memberEmail.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        if (memberService.getMemberByEmail(memberEmail) == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        if (searchRequest == null) {
            searchRequest = new OrderSearchRequestDto();
        }
        searchRequest.setMemberId(memberEmail);
        Page<OrderDto> orders = orderService.getOrders(searchRequest, pageable);

        return ResponseEntity.ok(orders);
    }

    /**
     * 주문 상세조회
     *
     * @param orderId 주문 아이디
     * @return 주문상세 DTO
     */
    @GetMapping("/api/orders/{order-id}")
    public ResponseEntity<OrderDetail> getOrderDetail(@PathVariable("order-id") String orderId) {
        OrderDetail orderDetail = orderService.getOrderDetail(orderId);
        return ResponseEntity.ok(orderDetail);
    }


    /**
     * 비회원 주문조회 접근
     *
     * @param accessRequest 주문번호, 비회원주문 조회용 비밀번호 DTO
     * @return 주문 ID
     */
    @PostMapping("/api/orders/non-member/access")
    public ResponseEntity<String> getNonMemberOrderDetail(@RequestBody NonMemberOrderDetailAccessRequestDto accessRequest) {
        String orderId = orderService.getNonMemberOrder(accessRequest);
        return ResponseEntity.ok(orderId);
    }

    /**
     * 주문상품 구매확정
     *
     * @param orderProductId 주문상품 ID
     * @return void
     */
    @PutMapping("/api/orders/order-products/{order-product-id}/purchase-confirm")
    public ResponseEntity<Void> purchaseConfirm(@PathVariable("order-product-id") Long orderProductId) {
        orderProductService.purchaseConfirm(orderProductId);
        return ResponseEntity.ok().build();
    }

}
