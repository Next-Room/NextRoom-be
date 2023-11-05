package com.nextroom.nextRoomServer.dto;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.JsonIOException;
import com.nextroom.nextRoomServer.domain.Subscription;
import com.nextroom.nextRoomServer.enums.EnumModel;
import com.nextroom.nextRoomServer.enums.SubscriptionPlan;
import com.nextroom.nextRoomServer.enums.UserStatus;
import com.nextroom.nextRoomServer.util.Timestamped;

import lombok.Getter;

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
        private final String plan;
        private final String description;
        private final Integer originPrice;
        private final Integer sellPrice;

        public SubscriptionPlanResponse(EnumModel enumModel) {
            this.plan = enumModel.getKey();
            this.description = enumModel.getDescription();
            this.originPrice = enumModel.getOriginPrice();
            this.sellPrice = enumModel.getSellPrice();
        }
    }
}
