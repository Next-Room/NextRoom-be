package com.nextroom.nextRoomServer.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nextroom.nextRoomServer.domain.Shop;

public interface ShopRepository extends JpaRepository<Shop, Long> {
    // 탈퇴한 업체의 adminCode가 신규 업체에 재발급되면 안 되므로 탈퇴 여부를 가리지 않는다.
    boolean existsByAdminCode(String adminCode);

    Optional<Shop> findByEmailAndGoogleSubAndDeletedAtIsNull(String email, String googleSub);

    Optional<Shop> findByEmailAndGoogleSubIsNullAndDeletedAtIsNull(String email);
}
