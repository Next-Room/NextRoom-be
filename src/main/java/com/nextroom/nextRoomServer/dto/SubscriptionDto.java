package com.nextroom.nextRoomServer.dto;

import java.time.LocalDate;

import com.nextroom.nextRoomServer.domain.Product;
import com.nextroom.nextRoomServer.domain.Subscription;
import com.nextroom.nextRoomServer.enums.UserStatus;
import com.nextroom.nextRoomServer.util.Timestamped;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class SubscriptionDto {
    @Getter
    public static class UpdateSubscription {
        private MessageData message;
        private String subscription;
    }

    @Getter
    public static class MessageData {
        private String data;
        private String messageId;
        private String publishTime;

    }

    @Getter
    public static class PublishedMessage {
        private String version;
        private String packageName;
        private String eventTimeMillis;
        private SubscriptionNotification subscriptionNotification;
        private TestNotification testNotification;

        public boolean isTestNotification() {
            return this.testNotification != null;
        }
    }

    @Getter
    public static class SubscriptionNotification {
        private String version;
        private int notificationType;
        private String purchaseToken;
        private String subscriptionId;
    }

    @Getter
    public static class TestNotification {
        private String version;
    }

    @Getter
    public static class SubscriptionInfoResponse {
        private final Long id;
        private final String name;
        private final UserStatus status;
        private final LocalDate startDate;
        private final LocalDate expiryDate;
        private final String createdAt;

        public SubscriptionInfoResponse(Subscription subscription) {
            this.id = subscription.getId();
            this.name = subscription.getShop().getName();
            this.status = subscription.getStatus();
            this.startDate = subscription.getStartDate();
            this.expiryDate = subscription.getExpiryDate();
            this.createdAt = Timestamped.dateTimeFormatter(subscription.getCreatedAt());
        }
    }

    @Getter
    public static class UserStatusResponse {
        private final Long id;
        private final UserStatus status;

        public UserStatusResponse(Subscription subscription) {
            this.id = subscription.getId();
            this.status = subscription.getStatus();
        }
    }

    @Getter
    @Builder
    public static class SubscriptionPlanResponse {
        private final String url;
        private final List<SubscriptionPlan> plans;
    }

    @Getter
    public static class SubscriptionPlan {
        private final Long id;
        private final String subscriptionProductId;
        private final String planId;
        private final String productName;
        private final String description;
        private final String subDescription;
        private final Integer originPrice;
        private final Integer sellPrice;
        private final Integer discountRate;

        public SubscriptionPlan(Product product) {
            this.id = product.getId();
            this.subscriptionProductId = product.getSubscriptionProductId();
            this.planId = product.getPlanId();
            this.productName = product.getProductName();
            this.description = product.getDescription();
            this.subDescription = product.getSubDescription();
            this.originPrice = product.getOriginPrice();
            this.sellPrice = product.getSellPrice();
            this.discountRate = product.getDiscountRate();
        }
    }

    @Schema(description = "구독 결제 요청 request")
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor(force = true)
    public static class PurchaseSubscription {
        @Schema(description = "purchaseToken(결제 토큰)")
        private final String purchaseToken;
    }
}
