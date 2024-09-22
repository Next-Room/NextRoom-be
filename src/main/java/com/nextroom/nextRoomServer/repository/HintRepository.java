package com.nextroom.nextRoomServer.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nextroom.nextRoomServer.domain.Hint;
import com.nextroom.nextRoomServer.domain.Theme;

import java.util.List;

public interface HintRepository extends JpaRepository<Hint, Long> {
    boolean existsByThemeAndHintCode(Theme theme, String hintCode);

    List<Hint> findAllByThemeId(Long themeId);
}