package com.nextroom.nextRoomServer.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.nextroom.nextRoomServer.dto.AuthDto;
import com.nextroom.nextRoomServer.enums.UserStatus;
import com.nextroom.nextRoomServer.exceptions.CustomException;
import com.nextroom.nextRoomServer.security.SecurityUtil;
import org.hibernate.annotations.Comment;

import com.nextroom.nextRoomServer.util.Timestamped;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static com.nextroom.nextRoomServer.exceptions.StatusCode.NOT_PERMITTED;
import static com.nextroom.nextRoomServer.exceptions.StatusCode.SHOP_ALREADY_WITHDRAWN;
import static com.nextroom.nextRoomServer.exceptions.StatusCode.SUBSCRIPTION_NOT_PERMITTED;

@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Shop extends Timestamped {

    private static final int EMAIL_MAX_LENGTH = 255;
    private static final String WITHDRAWN_EMAIL_PREFIX = "withdrawn_";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "shop_id", nullable = false)
    private Long id;

    @Column
    private String email;

    @Column
    private String googleSub;

    @Column
    private String password;

    @Column
    private String name;

    @Comment(value = "1: 웹(홈페이지)에서 PC로 들어온 유저, 2: 웹(홈페이지)에서 모바일로 들어온 유저, 3: 앱에서 들어온 유저")
    @Column
    private Integer type;

    @Column
    private String signupSource;

    @Column
    private String comment;

    @Column
    private Boolean adsConsent;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Authority authority;

    @Column
    private LocalDateTime lastLoginAt;

    @Column
    private LocalDateTime deletedAt;

    @OneToMany(mappedBy = "shop", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Theme> themes = new ArrayList<>();

    // 전자상거래법 제6조 / 시행령 제6조에 따라 결제 기록은 탈퇴 후에도 5년간 보존한다.
    // 따라서 shop 삭제가 payment로 전파되면 안 된다.
    @OneToMany(mappedBy = "shop")
    @Builder.Default
    private List<Payment> payments = new ArrayList<>();

    @OneToOne(mappedBy = "shop", cascade = CascadeType.ALL, orphanRemoval = true)
    private Subscription subscription;

    public void updateLastLoginAt() {
        this.lastLoginAt = LocalDateTime.now();
    }

    public void checkAuthorized() {
        if (!Objects.equals(this.id, SecurityUtil.getCurrentShopId())) {
            throw new CustomException(NOT_PERMITTED);
        }
    }

    public boolean isSubscription() {
        return this.subscription.getStatus() == UserStatus.SUBSCRIPTION;
    }

    public void validateSubscriptionInNeed(boolean needed) {
        if (!needed) { return; }
        if (this.subscription == null || !this.isSubscription()) {
            throw new CustomException(SUBSCRIPTION_NOT_PERMITTED);
        }
    }

    public void setAllUseTimerUrl(boolean active) {
        this.themes.forEach(theme -> Optional.ofNullable(theme.getTimerImageUrl())
                .ifPresent(it -> theme.setUseTimerUrl(active)));
    }

    public boolean isCompleteSignUp() {
        return this.name != null && !this.name.isEmpty();
    }

    public void updateShopInfo(AuthDto.ShopUpdateRequestDto request) {
        this.name = request.getName();
        this.signupSource = request.getSignupSource();
        this.comment = request.getComment();
        this.type = request.getType();
        this.adsConsent = request.getAdsConsent();
        this.lastLoginAt = LocalDateTime.now();
    }

    public boolean isWithdrawn() {
        return this.deletedAt != null;
    }

    /**
     * 회원 탈퇴 처리. 행을 지우지 않고 탈퇴 표시만 남긴다.
     * 결제 기록 보존 의무 때문에 하드 딜리트를 쓰지 않는다.
     * 같은 이메일로 재가입할 수 있도록 이메일을 변형해 원본 값을 비워 준다.
     */
    public void withdraw() {
        if (this.isWithdrawn()) {
            throw new CustomException(SHOP_ALREADY_WITHDRAWN);
        }
        this.deletedAt = LocalDateTime.now();
        this.email = toWithdrawnEmail(this.email, this.id);
        this.password = null;
    }

    private static String toWithdrawnEmail(String email, Long shopId) {
        if (email == null) {
            return null;
        }
        String withdrawn = WITHDRAWN_EMAIL_PREFIX + shopId + "_" + email;
        return withdrawn.length() > EMAIL_MAX_LENGTH
            ? withdrawn.substring(0, EMAIL_MAX_LENGTH)
            : withdrawn;
    }
}
