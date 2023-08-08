package com.nextroom.oescape.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nextroom.oescape.domain.Shop;

public interface ShopRepository extends JpaRepository<Shop, Long> {
    boolean existsByAdminCode(String adminCode);

    Optional<Shop> findByAdminCode(String adminCode);
}
