package com.nextroom.nextroom_server.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nextroom.nextroom_server.domain.Hint;
import com.nextroom.nextroom_server.domain.Theme;

public interface HintRepository extends JpaRepository<Hint, Long> {
    boolean existsByThemeAndHintCode(Theme theme, String hintCode);
}