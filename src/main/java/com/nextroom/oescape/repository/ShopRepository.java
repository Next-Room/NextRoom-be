package com.nextroom.oescape.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nextroom.oescape.domain.Shop;

public interface ShopRepository extends JpaRepository<Shop, Long> {
}
