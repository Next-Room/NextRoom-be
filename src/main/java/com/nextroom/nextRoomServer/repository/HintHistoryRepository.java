package com.nextroom.nextRoomServer.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nextroom.nextRoomServer.domain.HintHistory;

public interface HintHistoryRepository extends JpaRepository<HintHistory, Long> {
}
