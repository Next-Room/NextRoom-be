package com.nextroom.nextRoomServer.dto;

import com.nextroom.nextRoomServer.domain.Subscription;
import com.nextroom.nextRoomServer.enums.EnumModel;
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
    public static class SubscriptionNotification {
        private String version;
        private Integer notificationType;
        private String purchaseToken;
        private String subscriptionId;
    }

    @Getter
    public static class PublishedMessage {
        private String version;
        private String packageName;
        private String eventTimeMillis;
        private SubscriptionNotification subscriptionNotification;
    }

    @Getter
    public static class SubscriptionInfoResponse {
        private final Long id;
        private final SubscriptionPlan subStatus;
        private final String expiryDate;
        private final String createdAt;

        public SubscriptionInfoResponse(Subscription subscription) {
            this.id = subscription.getId();
            this.subStatus = subscription.getPlan();
            this.expiryDate = subscription.getExpiryDate().toString();
            this.createdAt = Timestamped.dateTimeFormatter(subscription.getCreatedAt());
        }
    }

    @Getter
    public static class UserStatusResponse {
        private final Long id;
        private final UserStatus userStatus;
        private final String expiryDate;
        private final String createdAt;

        public UserStatusResponse(Subscription subscription) {
            this.id = subscription.getId();
            this.userStatus = subscription.getStatus();
            this.expiryDate = subscription.getExpiryDate().toString();
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

        public SubscriptionPlanResponse(EnumModel enumModel) {
            this.id = enumModel.getId();
            this.plan = enumModel.getPlan();
            this.description = enumModel.getDescription();
            this.originPrice = enumModel.getOriginPrice();
            this.sellPrice = enumModel.getSellPrice();
        }
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor(force = true)
    public static class PurchaseSubscription {
        private final String purchaseToken;
        private final String subscriptionId;
    }
}
