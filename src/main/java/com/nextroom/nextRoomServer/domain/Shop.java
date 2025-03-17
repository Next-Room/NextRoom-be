package com.nextroom.nextRoomServer.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

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
import static com.nextroom.nextRoomServer.exceptions.StatusCode.SUBSCRIPTION_NOT_PERMITTED;

@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Shop extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "shop_id", nullable = false)
    private Long id;

    @Column
    private String email;

    @Column
    private String googleSub;

    @Column(nullable = false, length = 5)
    private String adminCode;

    @Column
    private String password;

    @Column
    private String name;

    @Comment(value = "1: 웹(홈페이지)에서 PC로 들어온 유저, 2: 웹(홈페이지)에서 모바일로 들어온 유저, 3: 앱에서 들어온 유저")
    @Column
    private Integer type;

    @Column
    private String comment;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Authority authority;

    @Column
    private LocalDateTime lastLoginAt;

    @OneToMany(mappedBy = "shop", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Theme> themes = new ArrayList<>();

    @OneToMany(mappedBy = "shop", cascade = CascadeType.ALL, orphanRemoval = true)
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

    public boolean isNotCompleteSignUp() {
        return this.name == null || this.name.isEmpty();
    }
}
