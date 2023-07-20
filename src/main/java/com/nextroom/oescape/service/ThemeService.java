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
    public void addTheme(Shop shop, ThemeDto.AddThemeRequest request) {
        Theme theme = Theme.builder()
            .title(request.getTitle())
            .timeLimit(request.getTimeLimit())
            .shop(shop)
            .build();

        themeRepository.save(theme);
    }

    @Transactional(readOnly = true)
    public List<ThemeDto.ThemeListResponse> getThemeList(Shop shop) {
        List<Theme> themeList = themeRepository.findAllByShop(shop);
        if (themeList.size() == 0) {
            throw new CustomException(THEME_NOT_FOUND);
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
            () -> new CustomException(THEME_NOT_FOUND)
        );
        theme.update(request);
    }

    public void removeTheme(Shop shop, ThemeDto.RemoveThemeRequest request) {
        Theme theme = themeRepository.findByIdAndShop(request.getId(), shop).orElseThrow(
            () -> new CustomException(THEME_NOT_FOUND)
        );
        themeRepository.delete(theme);
    }
}
