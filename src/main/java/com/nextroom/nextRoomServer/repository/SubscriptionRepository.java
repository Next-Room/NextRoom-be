package com.nextroom.nextRoomServer.repository;

import com.nextroom.nextRoomServer.domain.Subscription;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    Optional<Subscription> findByShopId(Long id);

    Optional<Subscription> findByPurchaseToken(String purchaseToken);
}
