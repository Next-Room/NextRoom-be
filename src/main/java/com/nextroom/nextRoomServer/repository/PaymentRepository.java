package com.nextroom.nextRoomServer.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nextroom.nextRoomServer.domain.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByOrderId(String orderId);

    List<Payment> findAllByShopId(Long shopId);

    Optional<Payment> findFirstByPurchaseTokenOrderByCreatedAt(String purchaseToken);
}
