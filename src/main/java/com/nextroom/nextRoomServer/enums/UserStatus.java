package com.nextroom.nextRoomServer.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserStatus {
    FREE("무료 체험"),
    SUBSCRIPTION("구독"),
    SUBSCRIPTION_EXPIRATION("구독 만료");

    private final String status;
}
