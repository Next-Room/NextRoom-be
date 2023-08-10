package com.nextroom.nextRoomServer.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nextroom.nextRoomServer.domain.RefreshToken;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByKey(String key);
}