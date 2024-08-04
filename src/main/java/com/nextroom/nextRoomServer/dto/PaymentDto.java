package com.nextroom.nextRoomServer.dto;

import java.time.LocalDateTime;

import com.nextroom.nextRoomServer.domain.Payment;

import lombok.Builder;
import lombok.Getter;

public class PaymentDto {

    @Getter
    @Builder
    public static class Meta {

        private final String orderId;
        private final Integer type;
        private final LocalDateTime createdAt;
    }

    @Getter
    @Builder
    public static class Detail {

        private final String orderId;
        private final Integer type;
        private final LocalDateTime createdAt;
        private final String purchaseToken;
        private final String receipt;  // FIXME: ui 픽스 후 유저 조회용 데이터로 변환
    }

    public static Meta toMeta(Payment payment) {
        return Meta.builder()
            .orderId(String.valueOf(payment.getOrderId()))
            .type(payment.getType())
            .createdAt(payment.getCreatedAt())
            .build();
    }

    public static Detail toDetail(Payment payment) {
        return Detail.builder()
            .orderId(String.valueOf(payment.getOrderId()))
            .type(payment.getType())
            .createdAt(payment.getCreatedAt())
            .purchaseToken(payment.getPurchaseToken())
            .receipt(payment.getReceipt())
            .build();
    }
}
