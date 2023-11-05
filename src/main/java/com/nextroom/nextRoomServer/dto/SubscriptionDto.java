package com.nextroom.nextRoomServer.dto;

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
}
