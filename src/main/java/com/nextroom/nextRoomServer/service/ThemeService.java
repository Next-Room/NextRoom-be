package com.nextroom.nextRoomServer.service;

import static com.nextroom.nextRoomServer.exceptions.StatusCode.*;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nextroom.nextRoomServer.domain.Shop;
import com.nextroom.nextRoomServer.domain.Theme;
import com.nextroom.nextRoomServer.dto.ThemeDto;
import com.nextroom.nextRoomServer.exceptions.CustomException;
import com.nextroom.nextRoomServer.repository.ShopRepository;
import com.nextroom.nextRoomServer.repository.SubscriptionRepository;
import com.nextroom.nextRoomServer.repository.ThemeRepository;
import com.nextroom.nextRoomServer.security.SecurityUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ThemeService {
    private final ThemeRepository themeRepository;
    private final ShopRepository shopRepository;
    private final SubscriptionRepository subscriptionRepository;

    private Shop getShop() {
        return shopRepository.findById(SecurityUtil.getCurrentShopId())
            .orElseThrow(() -> new CustomException(TARGET_SHOP_NOT_FOUND));
    }

    private Theme getTheme(Long themeId) {
        return themeRepository.findById(themeId)
            .orElseThrow(() -> new CustomException(TARGET_THEME_NOT_FOUND));
    }

    private Theme getThemeByThemeIdAndShop(Long themeId) {
        Theme theme = getTheme(themeId);
        if (!Objects.equals(theme.getShop().getId(), SecurityUtil.getCurrentShopId())) {
            throw new CustomException(NOT_PERMITTED);
        }
        return theme;
    }

    @Transactional
    public ThemeDto.AddThemeResponse addTheme(ThemeDto.AddThemeRequest request) {
        //        checkThemeLimitCount();

        Theme theme = Theme.builder()
            .title(request.getTitle())
            .timeLimit(request.getTimeLimit())
            .hintLimit(request.getHintLimit())
            .shop(getShop())
            .build();

        return new ThemeDto.AddThemeResponse(themeRepository.save(theme).getId());
    }

    //    private Integer getThemeLimitCount() {
    //        Subscription subscription = subscriptionRepository.findByShopId(SecurityUtil.getRequestedShopId()).orElseThrow(
    //            () -> new CustomException(TARGET_SHOP_NOT_FOUND));
    //
    //        return subscription.getPlan().getThemeLimitCount();
    //    }
    //
    //    private Integer getThemeCount() {
    //        return themeRepository.countByShopId(SecurityUtil.getRequestedShopId());
    //    }
    //
    //    private void checkThemeLimitCount() {
    //        if (getThemeLimitCount() <= getThemeCount()) {
    //            throw new CustomException(THEME_COUNT_EXCEEDED);
    //        }
    //    }

    @Transactional(readOnly = true)
    public List<ThemeDto.ThemeListResponse> getThemeList() {
        List<Theme> themeList = themeRepository.findAllByShopId(SecurityUtil.getCurrentShopId());
        return themeList.stream()
            .map(ThemeDto.ThemeListResponse::new)
            .collect(Collectors.toList());
    }

    @Transactional
    public void editTheme(ThemeDto.EditThemeRequest request) {
        Theme theme = getThemeByThemeIdAndShop(request.getId());
        theme.update(request);
    }

    public void removeTheme(ThemeDto.RemoveThemeRequest request) {
        Theme theme = getThemeByThemeIdAndShop(request.getId());
        themeRepository.delete(theme);
    }
}
