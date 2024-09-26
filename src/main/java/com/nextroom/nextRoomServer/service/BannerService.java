package com.nextroom.nextRoomServer.service;

import com.nextroom.nextRoomServer.domain.Banner;
import com.nextroom.nextRoomServer.dto.BannerDto;
import com.nextroom.nextRoomServer.repository.BannerRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BannerService {

    private final BannerRepository bannerRepository;

    @Transactional(readOnly = true)
    public List<BannerDto> getBannerList() {
        return this.getActiveBannerList()
            .stream()
            .map(BannerDto::toDto)
            .toList();
    }

    private List<Banner> getActiveBannerList() {
        return bannerRepository.findAllByActiveTrue();
    }

}