package com.nextroom.nextRoomServer.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SubscriptionPlan {
    STARTER("2개의 테마를 등록할 수 있어요", 19900, 9900),
    PRO("5개의 테마를 등록할 수 있어요", 29900, 14900),
    ENTERPRISE("8개의 테마를 등록할 수 있어요", 39900, 19900);

    private final String description;
    private final Integer originPrice;
    private final Integer sellPrice;
}
