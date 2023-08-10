package com.nextroom.nextroom_server.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nextroom.nextroom_server.domain.Shop;

public interface ShopRepository extends JpaRepository<Shop, Long> {
    boolean existsByAdminCode(String adminCode);

    Optional<Shop> findByAdminCode(String adminCode);
}
