package com.nextroom.nextRoomServer.dto;

import java.time.LocalDate;

import com.nextroom.nextRoomServer.domain.Subscription;
import com.nextroom.nextRoomServer.enums.SubscriptionPlan;
import com.nextroom.nextRoomServer.enums.UserStatus;
import com.nextroom.nextRoomServer.util.Timestamped;

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
    }

    @Getter
    public static class SubscriptionNotification {
        private String version;
        private int notificationType;
        private String purchaseToken;
        private String subscriptionId;
    }

    @Getter
    public static class SubscriptionInfoResponse {
        private final Long id;
        private final LocalDate startDate;
        private final LocalDate expiryDate;
        private final String createdAt;

        public SubscriptionInfoResponse(Subscription subscription) {
            this.id = subscription.getId();
            this.startDate = subscription.getStartDate();
            this.expiryDate = subscription.getExpiryDate();
            this.createdAt = Timestamped.dateTimeFormatter(subscription.getCreatedAt());
        }
    }

    @Getter
    public static class UserStatusResponse {
        private final Long id;
        private final UserStatus userStatus;
        private final LocalDate expiryDate;
        private final LocalDate startDate;
        private final String createdAt;

        public UserStatusResponse(Subscription subscription) {
            this.id = subscription.getId();
            this.userStatus = subscription.getStatus();
            this.startDate = subscription.getStartDate();
            this.expiryDate = subscription.getExpiryDate();
            this.createdAt = Timestamped.dateTimeFormatter(subscription.getCreatedAt());
        }
    }

    @Getter
    public static class SubscriptionPlanResponse {
        private final String id;
        private final String plan;
        private final String description;
        private final Integer originPrice;
        private final Integer sellPrice;

        public SubscriptionPlanResponse(SubscriptionPlan plan) {
            this.id = plan.getId();
            this.plan = plan.getPlan();
            this.description = plan.getDescription();
            this.originPrice = plan.getOriginPrice();
            this.sellPrice = plan.getSellPrice();
        }
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor(force = true)
    public static class PurchaseSubscription {
        private final String purchaseToken;
    }
}
