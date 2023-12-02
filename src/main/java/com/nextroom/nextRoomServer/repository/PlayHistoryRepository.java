package com.nextroom.nextRoomServer.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nextroom.nextRoomServer.domain.PlayHistory;

public interface PlayHistoryRepository extends JpaRepository<PlayHistory, Long> {
    Integer countByThemeId(Long themeId);
}
