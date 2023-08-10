package com.nextroom.nextRoomServer.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nextroom.nextRoomServer.domain.Hint;
import com.nextroom.nextRoomServer.domain.Theme;

public interface HintRepository extends JpaRepository<Hint, Long> {
    boolean existsByThemeAndHintCode(Theme theme, String hintCode);
}