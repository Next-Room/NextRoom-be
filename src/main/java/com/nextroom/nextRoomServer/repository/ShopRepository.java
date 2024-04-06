package com.nextroom.nextRoomServer.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nextroom.nextRoomServer.domain.Shop;

public interface ShopRepository extends JpaRepository<Shop, Long> {
    boolean existsByEmail(String email);

    Optional<Shop> findByAdminCode(String adminCode);

    Optional<Shop> findByEmail(String email);
}
