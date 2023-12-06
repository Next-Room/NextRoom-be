package com.nextroom.nextRoomServer.domain;

import java.time.LocalDate;

import com.nextroom.nextRoomServer.enums.SubscriptionPlan;
import com.nextroom.nextRoomServer.enums.UserStatus;
import com.nextroom.nextRoomServer.util.Timestamped;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Subscription extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "subscription_id", nullable = false)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id", nullable = false)
    private Shop shop;

    @Enumerated(EnumType.STRING)
    private UserStatus status;
    @Enumerated(EnumType.STRING)
    private SubscriptionPlan plan;
    private LocalDate expiryDate;
    @Column(unique = true)
    private String purchaseToken;

//    public void renew(LocalDate expiryDate) {
//        this.expiryDate = expiryDate;
//    }
//
//    public void expire() {
//        this.status = UserStatus.EXPIRATION;
//    }
//
//    public void updateStatus(UserStatus userStatus, LocalDate expiryDate, SubscriptionPlan plan) {
//        this.status = userStatus;
//        this.expiryDate = expiryDate;
//        this.plan = plan;
//    }
}
