package com.nextroom.oescape.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nextroom.oescape.domain.Shop;
import com.nextroom.oescape.domain.Theme;

public interface ThemeRepository extends JpaRepository<Theme, Long> {
    Optional<Theme> findByTitle(String title);

    List<Theme> findAllByShop(Shop shop);

    Optional<Theme> findByIdAndShop(Long id, Shop shop);

    boolean existsByIdAndShop(Long themeId, Shop shop);
}
