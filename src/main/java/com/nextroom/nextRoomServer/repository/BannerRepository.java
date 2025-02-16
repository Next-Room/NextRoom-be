package com.nextroom.nextRoomServer.repository;

import com.nextroom.nextRoomServer.domain.Banner;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BannerRepository extends JpaRepository<Banner, Long> {

    List<Banner> findAllByActiveTrueOrderByIdDesc();

}