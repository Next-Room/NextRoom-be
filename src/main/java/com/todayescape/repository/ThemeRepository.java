package com.todayescape.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.todayescape.domain.Shop;
import com.todayescape.domain.Theme;

public interface ThemeRepository extends JpaRepository<Theme, Long> {
	Optional<Theme> findByTitle(String title);

	List<Theme> findAllByShop(Shop shop);
}
