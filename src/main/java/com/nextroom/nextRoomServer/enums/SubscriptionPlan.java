package com.nextroom.nextRoomServer.enums;

import java.util.Arrays;
import java.util.Objects;

public enum SubscriptionPlan implements EnumModel {
    MINI("2개의 테마를 등록할 수 있어요", 19900, 9900, "2"),
    MEDIUM("5개의 테마를 등록할 수 있어요", 29900, 14900, "3"),
    LARGE("8개의 테마를 등록할 수 있어요", 39900, 19900, "4");

    private final String description;
    private final Integer originPrice;
    private final Integer sellPrice;
    private final String planId;

    SubscriptionPlan(String description, Integer originPrice, Integer sellPrice, String planId) {
        this.description = description;
        this.originPrice = originPrice;
        this.sellPrice = sellPrice;
        this.planId = planId;
    }

    @Override
    public String getKey() {
        return name();
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public Integer getOriginPrice() {
        return originPrice;
    }

    @Override
    public Integer getSellPrice() {
        return sellPrice;
    }

    @Override
    public String getPlanId() {
        return planId;
    }

    public static SubscriptionPlan getSubscriptionPlanByPlanId(String planId) {
        return Arrays.stream(SubscriptionPlan.values())
            .filter(subscriptionPlan -> Objects.equals(subscriptionPlan.getPlanId(), planId))
            .toList().get(0);
    }
}
