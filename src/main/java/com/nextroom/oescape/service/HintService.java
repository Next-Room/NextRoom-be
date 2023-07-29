package com.nextroom.oescape.service;

import static com.nextroom.oescape.exceptions.StatusCode.*;

import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nextroom.oescape.domain.Hint;
import com.nextroom.oescape.domain.Theme;
import com.nextroom.oescape.dto.HintDto;
import com.nextroom.oescape.exceptions.CustomException;
import com.nextroom.oescape.repository.HintRepository;
import com.nextroom.oescape.repository.ThemeRepository;
import com.nextroom.oescape.security.SecurityUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HintService {
    private final HintRepository hintRepository;
    private final ThemeRepository themeRepository;

    @Transactional
    public void addHint(HintDto.AddHintRequest request) {
        Theme theme = themeRepository.findById(
                request.getThemeId()) // TODO optimize by making method get theme from shop
            .orElseThrow(() -> new CustomException(THEME_NOT_FOUND));

        if (hintRepository.existsByThemeAndHintCode(theme, request.getHintCode())) {
            throw new CustomException(HINT_CODE_CONFLICT);
        }

        Hint hint = Hint.builder()
            .theme(theme)
            .hintTitle(request.getHintTitle())
            .hintCode(request.getHintCode())
            .contents(request.getContents())
            .answer(request.getAnswer())
            .progress(request.getProgress())
            .build();

        if (!Objects.equals(theme.getShop().getId(), SecurityUtil.getRequestedShopId())) {
            throw new CustomException(NOT_PERMITTED);
        }

        hintRepository.save(hint);
    }

    @Transactional(readOnly = true)
    public List<HintDto.HintListResponse> getHintList(Long themeId) {
        Theme theme = themeRepository.findById(
                themeId) // TODO optimize by making method get theme from shop
            .orElseThrow(() -> new CustomException(THEME_NOT_FOUND));

        if (!Objects.equals(theme.getShop().getId(), SecurityUtil.getRequestedShopId())) {
            throw new CustomException(NOT_PERMITTED);
        }

        return theme.getHints().stream().map(Hint::toHintListResponse).toList();
    }

    public Object getHintListByThemeId(Long themeId) {
        Theme theme = themeRepository.findById(themeId).orElseThrow(() -> new CustomException(TARGET_THEME_NOT_FOUND));

        return theme.getHints().stream().map(Hint::toHintListResponse).toList();
    }

    @Transactional
    public void editHint(HintDto.EditHintRequest request) {
        Hint hint = hintRepository.findById(request.getId()).orElseThrow(
            () -> new CustomException(TARGET_HINT_NOT_FOUND)
        );

        if (!Objects.equals(hint.getTheme().getShop().getId(), SecurityUtil.getRequestedShopId())) {
            throw new CustomException(NOT_PERMITTED);
        }

        hint.update(request);
    }

    public void removeHint(HintDto.RemoveHintRequest request) {
        Hint hint = hintRepository.findById(request.getId()).orElseThrow(
            () -> new CustomException(HINT_NOT_FOUND)
        );

        if (!Objects.equals(hint.getTheme().getShop().getId(), SecurityUtil.getRequestedShopId())) {
            throw new CustomException(NOT_PERMITTED);
        }

        hintRepository.delete(hint);
    }
}