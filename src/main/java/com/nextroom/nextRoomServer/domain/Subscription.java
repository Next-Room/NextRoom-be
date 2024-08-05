package com.nextroom.nextRoomServer.domain;

import static com.nextroom.nextRoomServer.enums.UserStatus.*;

import java.time.LocalDate;

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
import jakarta.persistence.ManyToOne;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @Enumerated(EnumType.STRING)
    private UserStatus status;

    @Column
    private LocalDate startDate;

    @Column
    private LocalDate expiryDate;

    public void renew(LocalDate startDate, LocalDate expiryDate) {
        this.startDate = startDate;
        this.expiryDate = expiryDate;
    }

    public void expire() {
        this.status = UserStatus.SUBSCRIPTION_EXPIRATION;
    }

    public void checkStatus() {
        if (SUBSCRIPTION.equals(this.status) && this.expiryDate.isBefore(Timestamped.getToday())) {
            this.expire();
        }
    }
}
