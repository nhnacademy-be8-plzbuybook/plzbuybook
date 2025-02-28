package com.nhnacademy.book.coupon.service.impl;

import com.nhnacademy.book.coupon.CouponClient;
import com.nhnacademy.book.coupon.dto.*;
import com.nhnacademy.book.coupon.exception.CouponException;
import com.nhnacademy.book.coupon.service.CouponService;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Slf4j
@RequiredArgsConstructor
@Service
public class CouponServiceImpl implements CouponService {
    private final CouponClient couponClient;

    // 회원가입쿠폰 (welcome) 발급
    public String issueWelcomeCoupon(WelComeCouponRequestDto requestDto) {
        try {
            String issueWelcomeCoupon = couponClient.issueWelcomeCoupon(requestDto).getBody();

            if (issueWelcomeCoupon == null) {
                throw new CouponException("회원가입쿠폰 발급 에러");
            }

            return issueWelcomeCoupon;
        } catch (FeignException | CouponException e) {
            log.error("issueWelcomeCoupon Feign Exception: {}", e.getMessage());
            throw new CouponException(e.getMessage());
        }
    }

    // 생일쿠폰 발급
    public String issueBirthdayCoupon(BirthdayCouponRequestDto requestDto) {
        try {
            String issueBirthdayCoupon = couponClient.issueBirthdayCoupon(requestDto).getBody();

            if (issueBirthdayCoupon == null) {
                throw new CouponException("생일쿠폰 발급 에러");
            }

            return issueBirthdayCoupon;
        } catch (FeignException | CouponException e) {
            log.error("issueBirthdayCoupon Feign Exception: {}", e.getMessage());
            throw new CouponException(e.getMessage());
        }
    }

    // 주문금액 할인계산 검증
    public ValidationCouponCalculationResponseDto validateCouponCalculation(Long couponId, ValidationCouponCalculationRequestDto validationCouponCalculationRequestDto) {
        try {
            ValidationCouponCalculationResponseDto validateCouponCalculation = couponClient.validateCouponCalculation(couponId, validationCouponCalculationRequestDto).getBody();

            if (validateCouponCalculation == null) {
                throw new CouponException("주문금액 할인계산 검증 에러");
            }

            return validateCouponCalculation;
        } catch (FeignException | CouponException e) {
            log.error("validateCouponCalculation Feign Exception: {}", e.getMessage());
            throw new CouponException(e.getMessage());
        }
    }

    // 쿠폰 환불
    public String refundCoupon(RefundCouponRequestDto refundCouponRequestDto) {
        try {
            String refundCoupon = couponClient.refundCoupon(refundCouponRequestDto).getBody();

            if (refundCoupon == null) {
                throw new CouponException("쿠폰환불 에러");
            }

            return refundCoupon;
        } catch (FeignException | CouponException e) {
            log.error("refundCoupon Feign Exception: {}", e.getMessage());
            throw new CouponException(e.getMessage());
        }
    }

    @Override
    public String useCoupon(Long couponId) {
        try {
            String useCoupon = couponClient.useCoupon(couponId).getBody();

            if (useCoupon == null) {
                throw new CouponException("쿠폰사용 에러");
            }

            return useCoupon;
        } catch (FeignException | CouponException e) {
            log.error("useCoupon Feign Exception: {}", e.getMessage());
            throw new CouponException(e.getMessage());
        }
    }

    @Override
    public String cancelCoupon(Long couponId) {
        try {
            String cancelCoupon = couponClient.cancelCoupon(couponId).getBody();

            if (cancelCoupon == null) {
                throw new CouponException("쿠폰사용 취소 에러");
            }

            return cancelCoupon;
        } catch (FeignException | CouponException e) {
            log.error("cancelCoupon Feign Exception: {}", e.getMessage());
            throw new CouponException(e.getMessage());
        }
    }
}
