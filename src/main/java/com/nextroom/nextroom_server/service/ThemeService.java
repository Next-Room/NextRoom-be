package com.nextroom.nextroom_server.service;

import static com.nextroom.nextroom_server.exceptions.StatusCode.*;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nextroom.nextroom_server.domain.Shop;
import com.nextroom.nextroom_server.domain.Theme;
import com.nextroom.nextroom_server.dto.ThemeDto;
import com.nextroom.nextroom_server.exceptions.CustomException;
import com.nextroom.nextroom_server.repository.ShopRepository;
import com.nextroom.nextroom_server.repository.ThemeRepository;
import com.nextroom.nextroom_server.security.SecurityUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ThemeService {
    private final ThemeRepository themeRepository;
    private final ShopRepository shopRepository;

    private Shop validateShop() {
        return shopRepository.findById(SecurityUtil.getRequestedShopId())
            .orElseThrow(() -> new CustomException(TOKEN_UNAUTHORIZED));
    }

    private Shop validateAdminCode(String adminCode) {
        return shopRepository.findByAdminCode(adminCode)
            .orElseThrow(() -> new CustomException(TARGET_SHOP_NOT_FOUND));
    }

    private Theme validateTheme(Long themeId) {
        return themeRepository.findById(themeId)
            .orElseThrow(() -> new CustomException(TARGET_THEME_NOT_FOUND));
    }

    private Theme validateThemeIdAndShop(Long themeId) {
        Theme theme = validateTheme(themeId);
        if (!theme.getShop().equals(validateShop())) {
            throw new CustomException(NOT_PERMITTED);
        }
        return theme;
    }

    @Transactional
    public void addTheme(ThemeDto.AddThemeRequest request) {
        Theme theme = Theme.builder()
            .title(request.getTitle())
            .timeLimit(request.getTimeLimit())
            .hintLimit(request.getHintLimit())
            .shop(validateShop())
            .build();

        themeRepository.save(theme);
    }

    @Transactional(readOnly = true)
    public List<ThemeDto.ThemeListResponse> getThemeList(String adminCode) {
        Shop shop = (adminCode == null) ? validateShop() : validateAdminCode(adminCode);
        List<Theme> themeList = themeRepository.findAllByShop(shop);
        return themeList.stream()
            .map(ThemeDto.ThemeListResponse::new)
            .collect(Collectors.toList());
    }

    @Transactional
    public void editTheme(ThemeDto.EditThemeRequest request) {
        Theme theme = validateThemeIdAndShop(request.getId());
        theme.update(request);
    }

    public void removeTheme(ThemeDto.RemoveThemeRequest request) {
        Theme theme = validateThemeIdAndShop(request.getId());
        themeRepository.delete(theme);
    }
}
