package com.nextroom.oescape.service;

import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.nextroom.oescape.domain.Shop;
import com.nextroom.oescape.repository.ShopRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ShopService {
    private final ShopRepository shopRepository;

    public Shop extractShopFromAuthentication(Authentication authentication) {
        Long id = Long.parseLong(authentication.getName());
        System.out.println(id);
        return shopRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("잘못된 인증 정보입니다."));
    }
}
