package com.nextroom.oescape.service;

import static com.nextroom.oescape.exceptions.StatusCode.*;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nextroom.oescape.domain.Shop;
import com.nextroom.oescape.domain.Theme;
import com.nextroom.oescape.dto.ThemeDto;
import com.nextroom.oescape.exceptions.CustomException;
import com.nextroom.oescape.repository.ThemeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ThemeService {
    private final ThemeRepository themeRepository;

    @Transactional
    public ThemeDto.AddThemeResponse addTheme(Shop shop, ThemeDto.AddThemeRequest request) {
        Theme theme = Theme.builder()
            .title(request.getTitle())
            .timeLimit(request.getTimeLimit())
            .shop(shop)
            .build();

        Theme savedTheme = themeRepository.save(theme);

        return ThemeDto.AddThemeResponse.builder()
            .id(savedTheme.getId())
            .title(savedTheme.getTitle())
            .timeLimit(savedTheme.getTimeLimit())
            .build();
    }

    @Transactional(readOnly = true)
    public List<ThemeDto.ThemeListResponse> getThemeList(Shop shop) {
        List<Theme> themeList = themeRepository.findAllByShop(shop);
        if (themeList.size() == 0) {
            throw new CustomException(THEME_NOT_FOUNT);
        }
        List<ThemeDto.ThemeListResponse> themeListResponses = new ArrayList<>();
        for (Theme theme : themeList) {
            themeListResponses.add(ThemeDto.ThemeListResponse
                .builder()
                .id(theme.getId())
                .title(theme.getTitle())
                .timeLimit(theme.getTimeLimit())
                .build());
        }
        return themeListResponses;
    }

    @Transactional
    public void editTheme(Shop shop, ThemeDto.EditThemeRequest request) {
        Theme theme = themeRepository.findByIdAndShop(request.getId(), shop).orElseThrow(
            () -> new CustomException(THEME_NOT_FOUNT)
        );
        theme.update(request);
    }

    public void removeTheme(Shop shop, ThemeDto.RemoveRequest request) {
        Theme theme = themeRepository.findByIdAndShop(request.getId(), shop).orElseThrow(
            () -> new CustomException(THEME_NOT_FOUNT)
        );
        themeRepository.delete(theme);
    }
}
