package com.nextroom.nextRoomServer.enums;

import java.util.Arrays;
import java.util.Objects;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SubscriptionState {
    SUBSCRIPTION_STATE_UNSPECIFIED(1, "SUBSCRIPTION_STATE_UNSPECIFIED."), // 지정되지 않은 구독 상태입니다.
    SUBSCRIPTION_STATE_PENDING(2,
        "SUBSCRIPTION_STATE_PENDING."), // 구독이 생성되었지만 가입 중에 결제 대기 중입니다. 이 상태에서는 모든 상품이 결제 대기 중입니다.
    SUBSCRIPTION_STATE_ACTIVE(3,
        "SUBSCRIPTION_STATE_ACTIVE."), // 구독이 활성 상태입니다. - (1) 구독이 자동 갱신 요금제인 경우 하나 이상의 항목이 autoRenewEnabled이며 만료되지 않았습니다. - (2) 선불 요금제인 경우 하나 이상의 항목은 만료되지 않았습니다.
    SUBSCRIPTION_STATE_PAUSED(4,
        "SUBSCRIPTION_STATE_PAUSED."), // 구독이 일시중지되었습니다. 구독이 자동 갱신 요금제인 경우에만 상태를 확인할 수 있습니다. 이 상태에서는 모든 항목이 일시중지 상태입니다.
    SUBSCRIPTION_STATE_IN_GRACE_PERIOD(5,
        "SUBSCRIPTION_STATE_IN_GRACE_PERIOD."), // 구독이 유예 기간입니다. 구독이 자동 갱신 요금제인 경우에만 상태를 확인할 수 있습니다. 이 상태에서는 모든 항목이 유예 기간입니다.
    SUBSCRIPTION_STATE_ON_HOLD(6,
        "SUBSCRIPTION_STATE_ON_HOLD."), // 구독이 보류 중입니다 (정지됨). 구독이 자동 갱신 요금제인 경우에만 상태를 확인할 수 있습니다. 이 상태에서는 모든 항목이 보류됩니다.
    SUBSCRIPTION_STATE_CANCELED(7,
        "SUBSCRIPTION_STATE_CANCELED."), // 구독이 취소되었지만 아직 만료되지 않았습니다. 구독이 자동 갱신 요금제인 경우에만 상태를 확인할 수 있습니다. 모든 항목의 autoRenewEnabled가 false로 설정되었습니다.
    SUBSCRIPTION_STATE_EXPIRED(8, "SUBSCRIPTION_STATE_EXPIRED."), // 구독이 만료되었습니다. 모든 항목의 expiryTime이 과거입니다.
    SUBSCRIPTION_STATE_PENDING_PURCHASE_CANCELED(9,
        "SUBSCRIPTION_STATE_PENDING_PURCHASE_CANCELED."); // 대기 중인 구독 거래가 취소되었습니다. 대기 중인 구매가 기존 정기 결제에 대한 것이면 linkedPurchaseToken을 사용

    private final Integer type;
    private final String state;

    public static Integer getSubscriptionType(String state) {
        return Arrays.stream(SubscriptionState.values())
            .filter(SubscriptionState -> Objects.equals(SubscriptionState.getState(), state))
            .toList()
            .get(0)
            .getType();
    }
}
