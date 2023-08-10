package com.nextroom.nextroom_server.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nextroom.nextroom_server.domain.RefreshToken;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByKey(String key);
}