package com.nextroom.nextRoomServer.dto;

import com.nextroom.nextRoomServer.domain.Banner;

public record BannerDto(String description, String url, String imageUrl) {

    public static BannerDto toDto(Banner banner) {
        return new BannerDto(banner.getDescription(), banner.getUrl(), banner.getImageUrl());
    }

}