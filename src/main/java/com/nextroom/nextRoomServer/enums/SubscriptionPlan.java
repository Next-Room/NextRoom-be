package com.nextroom.nextRoomServer.enums;

public enum SubscriptionPlan implements EnumModel {
    MINI(2, "2개의 테마를 등록할 수 있어요", 19900, 9900),
    MEDIUM(3, "5개의 테마를 등록할 수 있어요", 29900, 14900),
    LARGE(4, "8개의 테마를 등록할 수 있어요", 39900, 19900);

    private final Integer id;
    private final String description;
    private final Integer originPrice;
    private final Integer sellPrice;

    SubscriptionPlan(Integer id, String description, Integer originPrice, Integer sellPrice) {
        this.id = id;
        this.description = description;
        this.originPrice = originPrice;
        this.sellPrice = sellPrice;
    }

    @Override
    public String getKey() {
        return name();
    }

    @Override
    public Integer getId() {
        return id;
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
