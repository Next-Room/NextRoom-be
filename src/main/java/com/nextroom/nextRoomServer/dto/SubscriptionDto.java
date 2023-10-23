package com.nextroom.nextRoomServer.dto;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.JsonIOException;

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

        public SubscriptionNotification(String JsonData) {
            Pattern pattern = Pattern.compile("\"SubscriptionNotification\":\\{(.*?)\\}");
            Matcher matcher = pattern.matcher(JsonData);

            if (!matcher.find()) {
                throw new JsonIOException("");
            }

            String notificationContent = matcher.group(1);

            Pattern subscriptionNotificationPattern = Pattern.compile("\\{.*"
                + "\"version\": (.*?),.*"
                + "\"notificationType\": (.*?),.*"
                + "\"purchaseToken\": (.*?),.*"
                + "\"subscriptionId\": (.*?).*"
                + "\\}");

            Matcher subscriptionNotificationMatcher = subscriptionNotificationPattern.matcher(notificationContent);

            if (!subscriptionNotificationMatcher.find()) {
                throw new JsonIOException("");
            }

            this.version = subscriptionNotificationMatcher.group(1);
            this.notificationType = Integer.parseInt(subscriptionNotificationMatcher.group(2));
            this.purchaseToken = subscriptionNotificationMatcher.group(3);
            this.subscriptionId = subscriptionNotificationMatcher.group(4);
        }
    }
}
