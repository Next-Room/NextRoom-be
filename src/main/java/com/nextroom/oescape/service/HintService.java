package com.nextroom.oescape.service;

import static com.nextroom.oescape.exceptions.StatusCode.*;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nextroom.oescape.domain.Hint;
import com.nextroom.oescape.domain.Shop;
import com.nextroom.oescape.domain.Theme;
import com.nextroom.oescape.dto.HintDto;
import com.nextroom.oescape.exceptions.CustomException;
import com.nextroom.oescape.repository.HintRepository;
import com.nextroom.oescape.repository.ThemeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HintService {
    private final HintRepository hintRepository;
    private final ThemeRepository themeRepository;

    @Transactional
    public void addHint(Shop shop, HintDto.AddHintRequest request) {
        validateTheme(shop, request.getThemeId());

        Theme theme = themeRepository.findById(
                request.getThemeId()) // TODO optimize by making method get theme from shop
            .orElseThrow(() -> new CustomException(THEME_NOT_FOUND));

        Hint hint = Hint.builder()
            .theme(theme)
            .hintTitle(request.getHintTitle())
            .hintCode(request.getHintCode())
            .contents(request.getContents())
            .answer(request.getAnswer())
            .progress(request.getProgress())
            .build();

        hintRepository.save(hint);
    }

    @Transactional(readOnly = true)
    public List<HintDto.HintListResponse> getHintList(Shop shop, Long themeId) {
        validateTheme(shop, themeId);

        Theme theme = themeRepository.findById(
                themeId) // TODO optimize by making method get theme from shop
            .orElseThrow(() -> new CustomException(THEME_NOT_FOUND));

        return theme.getHints().stream().map(Hint::toHintListResponse).toList();
    }

    @Transactional
    public void editHint(Shop shop, HintDto.EditHintRequest request) {
        Hint hint = hintRepository.findById(request.getId()).orElseThrow(
            () -> new CustomException(HINT_NOT_FOUND)
        );
        hint.update(request);
    }

    private void validateTheme(Shop shop, Long themeId) {
        if (!themeRepository.existsByIdAndShop(themeId, shop)) {
            throw new CustomException(THEME_NOT_FOUND);
        }
    }

    private void validateHint(Shop shop, Long hintId) {
        Boolean validity = false;
        for (Theme theme : shop.getThemes()) {
            if (hintRepository.existsById(hintId)) {
                validity = true;
            }
        }
        if (!validity) {
            throw new CustomException(HINT_NOT_FOUND);
        }
    }
}