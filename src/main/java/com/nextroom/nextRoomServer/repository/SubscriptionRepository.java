package com.nextroom.nextRoomServer.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nextroom.nextRoomServer.domain.Subscription;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    Optional<Subscription> findByShopId(Long id);
    Optional<Subscription> findByPurchaseToken(String purchaseToken);

    void deleteByShopId(Long shopId);
}
