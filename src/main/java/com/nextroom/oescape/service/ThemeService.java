package com.nextroom.oescape.service;

import static com.nextroom.oescape.exceptions.StatusCode.*;
import static com.nextroom.oescape.util.Timestamped.*;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nextroom.oescape.domain.Shop;
import com.nextroom.oescape.domain.Theme;
import com.nextroom.oescape.dto.ThemeDto;
import com.nextroom.oescape.exceptions.CustomException;
import com.nextroom.oescape.repository.ShopRepository;
import com.nextroom.oescape.repository.ThemeRepository;
import com.nextroom.oescape.security.SecurityUtil;

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
            .shop(validateShop())
            .build();

        themeRepository.save(theme);
    }

    @Transactional(readOnly = true)
    public List<ThemeDto.ThemeListResponse> getThemeList(String adminCode) {
        Shop shop = (adminCode == null) ? validateShop() : validateAdminCode(adminCode);
        List<Theme> themeList = themeRepository.findAllByShop(shop);
        return themeList.stream()
            .map(theme -> ThemeDto.ThemeListResponse.builder()
                .id(theme.getId())
                .title(theme.getTitle())
                .timeLimit(theme.getTimeLimit())
                .createdAt(dateTimeFormatter(theme.getCreatedAt()))
                .modifiedAt(dateTimeFormatter(theme.getModifiedAt()))
                .build())
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
