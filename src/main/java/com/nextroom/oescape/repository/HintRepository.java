package com.nextroom.oescape.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nextroom.oescape.domain.Hint;

public interface HintRepository extends JpaRepository<Hint, Long> {
}