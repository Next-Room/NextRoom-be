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
    private HintRepository hintRepository;
    private ThemeRepository themeRepository;

    @Transactional
    public void addHint(Shop shop, HintDto.AddHintRequest request) {
        validTheme(shop, request.getThemeId());

        Theme theme = themeRepository.findById(request.getThemeId())
            .orElseThrow(() -> new CustomException(THEME_NOT_FOUNT));

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

    private void validTheme(Shop shop, Long themeId) {
        if (themeRepository.existsByIdAndShop(themeId, shop)) {
            throw new CustomException(THEME_NOT_FOUNT);
        }
    }

    @Transactional
    public List<HintDto.HintListResponse> getHintList(Long themeId) {
        return null;
    }
}