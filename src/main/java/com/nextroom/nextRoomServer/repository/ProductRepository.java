package com.nextroom.nextRoomServer.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nextroom.nextRoomServer.domain.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findBySubscriptionProductId(String productId);
}
