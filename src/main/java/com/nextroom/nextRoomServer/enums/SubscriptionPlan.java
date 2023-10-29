package com.nextroom.nextRoomServer.enums;

public enum SubscriptionPlan implements EnumModel {
    MINI("2개의 테마를 등록할 수 있어요", 19900, 9900),
    MEDIUM("5개의 테마를 등록할 수 있어요", 29900, 14900),
    LARGE("8개의 테마를 등록할 수 있어요", 39900, 19900);

    private final String description;
    private final Integer originPrice;
    private final Integer sellPrice;

    SubscriptionPlan(String description, Integer originPrice, Integer sellPrice) {
        this.description = description;
        this.originPrice = originPrice;
        this.sellPrice = sellPrice;
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

}
