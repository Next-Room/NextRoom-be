package com.nextroom.oescape.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nextroom.oescape.domain.Hint;
import com.nextroom.oescape.domain.Theme;

public interface HintRepository extends JpaRepository<Hint, Long> {
    boolean existsByThemeAndHintCode(Theme theme, String hintCode);
}