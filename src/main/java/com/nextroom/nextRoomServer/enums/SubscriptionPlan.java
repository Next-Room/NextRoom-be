package com.nextroom.nextRoomServer.enums;

import com.nextroom.nextRoomServer.dto.SubscriptionDto.SubscriptionPlanResponse;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import lombok.Getter;

@Getter
public enum SubscriptionPlan {
    MINI("mini_subscription", "미니", "2개의 테마를 등록할 수 있어요", 19900, 9900, 2),
    MEDIUM("medium_subscription", "미디움", "5개의 테마를 등록할 수 있어요", 29900, 14900, 5),
    LARGE("large_subscription", "라지", "8개의 테마를 등록할 수 있어요", 39900, 19900, 8);

    private final String id;
    private final String plan;
    private final String description;
    private final Integer originPrice;
    private final Integer sellPrice;
    private final Integer themeLimitCount;

    SubscriptionPlan(String id, String plan, String description, Integer originPrice, Integer sellPrice,
        Integer themeLimitCount) {
        this.id = id;
        this.plan = plan;
        this.description = description;
        this.originPrice = originPrice;
        this.sellPrice = sellPrice;
        this.themeLimitCount = themeLimitCount;
    }

    public static SubscriptionPlan getSubscriptionPlanByPlanId(String planId) {
        return Arrays.stream(SubscriptionPlan.values())
            .filter(subscriptionPlan -> Objects.equals(subscriptionPlan.getId(), planId))
            .toList().get(0);
    }

    public static List<SubscriptionPlanResponse> toList() {
        return Arrays.stream(values())
            .map(SubscriptionPlanResponse::new)
            .toList();
    }

}
