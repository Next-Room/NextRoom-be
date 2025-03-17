package com.nextroom.nextRoomServer.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nextroom.nextRoomServer.domain.Shop;

public interface ShopRepository extends JpaRepository<Shop, Long> {
    boolean existsByAdminCode(String adminCode);
    Optional<Shop> findByEmailAndGoogleSub(String email, String googleSub);
    Optional<Shop> findByEmailAndGoogleSubIsNull(String email);
}
