package com.nextroom.nextRoomServer.repository;

import com.nextroom.nextRoomServer.domain.Payment;
import com.nextroom.nextRoomServer.domain.Subscription;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByTransactionId(String transactionId);

    List<Payment> findAllByShopId(Long shopId);

}
