package com.nextroom.nextRoomServer.enums;

import java.util.Arrays;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum NotificationType {
    SUBSCRIPTION_RECOVERED(1, "SUBSCRIPTION_RECOVERED"), // 정기 결제가 계정 보류에서 복구되었습니다.
    SUBSCRIPTION_RENEWED(2, "SUBSCRIPTION_RENEWED"), // 활성 정기 결제가 갱신되었습니다.
    SUBSCRIPTION_CANCELED(3, "SUBSCRIPTION_CANCELED"), // 정기 결제가 자발적으로 또는 비자발적으로 취소되었습니다. 자발적 취소의 경우 사용자가 취소할 때 전송됩니다.
    SUBSCRIPTION_PURCHASED(4, "SUBSCRIPTION_PURCHASED"), // 새로운 정기 결제가 구매되었습니다.
    SUBSCRIPTION_ON_HOLD(5, "SUBSCRIPTION_ON_HOLD"), // 정기 결제가 계정 보류 상태가 되었습니다(사용 설정된 경우).
    SUBSCRIPTION_IN_GRACE_PERIOD(6, "SUBSCRIPTION_IN_GRACE_PERIOD"), // 정기 결제가 유예 기간 상태로 전환되었습니다(사용 설정된 경우).
    SUBSCRIPTION_RESTARTED(7,
        "SUBSCRIPTION_RESTARTED"), // 사용자가 Play > 계정 > 정기 결제에서 정기 결제를 복원했습니다. 정기 결제가 취소되었지만 사용자가 복원할 때 아직 만료되지 않았습니다. 자세한 내용은 복원을 참고하세요.
    SUBSCRIPTION_PRICE_CHANGE_CONFIRMED(8, "SUBSCRIPTION_PRICE_CHANGE_CONFIRMED"), // 사용자가 정기 결제 가격 변경을 확인했습니다.
    SUBSCRIPTION_DEFERRED(9, "SUBSCRIPTION_DEFERRED"), // 구독 갱신 기한이 연장되었습니다.
    SUBSCRIPTION_PAUSED(10, "SUBSCRIPTION_PAUSED"), // 구독이 일시중지되었습니다.
    SUBSCRIPTION_PAUSE_SCHEDULE_CHANGED(11, "SUBSCRIPTION_PAUSE_SCHEDULE_CHANGED"), // 정기 결제 일시중지 일정이 변경되었습니다.
    SUBSCRIPTION_REVOKED(12, "SUBSCRIPTION_REVOKED"), // 정기 결제가 만료 시간 전에 사용자에 의해 취소되었습니다.
    SUBSCRIPTION_EXPIRED(13, "SUBSCRIPTION_EXPIRED"); // 정기 결제가 만료되었습니다.

    private final Integer type;
    private final String state;

    public static NotificationType of(int notificationType) {
        return Arrays.stream(NotificationType.values())
            .filter(it -> it.getType().equals(notificationType))
            .findFirst()
            .orElse(null);
    }

}
