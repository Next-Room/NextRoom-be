package com.nextroom.oescape.dto.response;

import java.util.List;

import com.nextroom.oescape.domain.Shop;
import com.nextroom.oescape.domain.Theme;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ShopResponseDto {
    private String adminCode;
    private String name;
    private List<Theme> themes;

    public static ShopResponseDto of(Shop shop) {
        return ShopResponseDto.builder()
                .adminCode(shop.getAdminCode())
                .name(shop.getName())
                .themes(shop.getThemes())
                .build();
    }
}
