package com.nextroom.nextRoomServer.service;

import static com.nextroom.nextRoomServer.exceptions.StatusCode.TARGET_SHOP_NOT_FOUND;
import static com.nextroom.nextRoomServer.exceptions.StatusCode.TARGET_THEME_NOT_FOUND;

import com.nextroom.nextRoomServer.domain.Shop;
import com.nextroom.nextRoomServer.domain.Theme;
import com.nextroom.nextRoomServer.dto.ThemeDto;
import com.nextroom.nextRoomServer.dto.ThemeDto.ThemeUrlRequest;
import com.nextroom.nextRoomServer.dto.ThemeDto.ThemeUrlResponse;
import com.nextroom.nextRoomServer.exceptions.CustomException;
import com.nextroom.nextRoomServer.repository.ShopRepository;
import com.nextroom.nextRoomServer.repository.ThemeRepository;
import com.nextroom.nextRoomServer.security.SecurityUtil;
import com.nextroom.nextRoomServer.util.aws.S3Component;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ThemeService {

    private final S3Component s3Component;

    private final ThemeRepository themeRepository;
    private final ShopRepository shopRepository;

    private static final String TYPE_TIMER = "timer";

    private Shop getShop() {
        return shopRepository.findById(SecurityUtil.getCurrentShopId())
            .orElseThrow(() -> new CustomException(TARGET_SHOP_NOT_FOUND));
    }

    public Theme validateThemeAndShop(Long themeId) {
        Theme theme = themeRepository.findById(themeId)
            .orElseThrow(() -> new CustomException(TARGET_THEME_NOT_FOUND));
        theme.getShop().checkAuthorized();
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
            .map(it -> {
                String timerImageUrl = s3Component.generatePresignedUrlForDownLoad(it.getShop().getId(), it.getId(), TYPE_TIMER, it.getTimerImageUrl());
                return new ThemeDto.ThemeListResponse(it, timerImageUrl);
            })
            .toList();
    }

    @Transactional
    public void editTheme(ThemeDto.EditThemeRequest request) {
        Theme theme = validateThemeAndShop(request.getId());
        theme.update(request);
    }

    public void removeTheme(ThemeDto.RemoveThemeRequest request) {
        Theme theme = validateThemeAndShop(request.getId());
        themeRepository.delete(theme);
    }

    public ThemeUrlResponse getTimerUrl(Long themeId) {
        Long shopId = this.validateThemeAndShop(themeId)
            .getShop()
            .getId();
        String timerUrl = s3Component.generatePresignedUrlForUpload(shopId, themeId, TYPE_TIMER);

        return new ThemeUrlResponse(themeId, timerUrl);
    }

    @Transactional
    public void addThemeTimerImage(final ThemeUrlRequest request) {
        Theme theme = validateThemeAndShop(request.getThemeId());
        Shop shop = theme.getShop();

        if (!shop.isSubscription()) {
            shop.setAllUseTimerUrl(false);
        }
        theme.updateTimerImageActive(request.getImageUrl());
    }

    @Transactional
    public void removeThemeTimerImage(Long themeId) {
        Theme theme = validateThemeAndShop(themeId);
        if (Objects.isNull(theme.getTimerImageUrl())) {
            return;
        }
        Long shopId = theme.getShop().getId();
        s3Component.deleteObjects(shopId, themeId, TYPE_TIMER, List.of(theme.getTimerImageUrl()));
        theme.removeTimerImage();
    }

    @Transactional
    public void activeThemeTimerUrl(ThemeDto.ThemeActiveUrlRequest request) {
        if (request.getActive().size() > 1) {
            this.getShop().validateSubscriptionInNeed(true);
        }

        Map<Long, Theme> themes = themeRepository.findAllById(request.getAllThemeIds())
            .stream()
            .peek(it -> it.getShop().checkAuthorized())
            .collect(Collectors.toMap(Theme::getId, it -> it));

        request.getActive()
            .forEach(themeId -> Optional.ofNullable(themes.get(themeId))
                .ifPresent(it -> it.setUseTimerUrl(true)));
        request.getDeactive()
            .forEach(themeId -> Optional.ofNullable(themes.get(themeId))
                .ifPresent(it -> it.setUseTimerUrl(false)));
    }
}
