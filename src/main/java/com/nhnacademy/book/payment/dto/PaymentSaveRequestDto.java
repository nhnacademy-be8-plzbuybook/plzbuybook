package com.nhnacademy.book.payment.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.nhnacademy.book.order.entity.Orders;
import com.nhnacademy.book.payment.entity.Payment;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@NoArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaymentSaveRequestDto {
    @JsonProperty("paymentKey")
    private String paymentKey;

    @JsonProperty("orderId")
    private String orderId;

    @JsonProperty("currency")
    private String currency;

    @JsonProperty("method")
    private String method;

    @JsonProperty("totalAmount")
    private BigDecimal totalAmount;

    @JsonProperty("approvedAt")
    private OffsetDateTime approvedAt;

    @JsonProperty("easyPay")
    private EasyPay easyPay;

    @JsonProperty("status")
    private String status;

    @JsonProperty("receipt")
    private Receipt receipt;

    @Builder
    public PaymentSaveRequestDto(String paymentKey, String orderId, String currency, String method,
                                 BigDecimal totalAmount, OffsetDateTime approvedAt, EasyPay easyPay, String status,
                                 Receipt receipt) {
        this.paymentKey = paymentKey;
        this.orderId = orderId;
        this.currency = currency;
        this.method = method;
        this.totalAmount = totalAmount;
        this.approvedAt = approvedAt;
        this.easyPay = easyPay;
        this.status = status;
        this.receipt = receipt;
    }

    public Payment toEntity(Orders order) {
        return Payment.builder()
                .status(status)
                .paymentKey(paymentKey)
                .amount(totalAmount)
                .method(method)
                .recordedAt(approvedAt.toLocalDateTime())
                .easyPayProvider(easyPay.getProvider())
                .orders(order)
                .build();
    }

    @Getter
    @Setter
    public static class EasyPay {
        @JsonProperty("provider")
        private String provider;

        @JsonCreator
        public EasyPay(@JsonProperty("provider") String provider) {
            this.provider = provider;
        }
    }

    @Getter
    @Setter
    public static class Receipt {
        @JsonProperty("url")
        private String url;

        @JsonCreator
        public Receipt(@JsonProperty("url") String url) {
            this.url = url;
        }
    }
}
