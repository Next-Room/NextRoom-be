package com.nextroom.nextRoomServer.service;

import static com.nextroom.nextRoomServer.exceptions.StatusCode.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.nextroom.nextRoomServer.util.aws.S3Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nextroom.nextRoomServer.domain.Hint;
import com.nextroom.nextRoomServer.domain.Theme;
import com.nextroom.nextRoomServer.dto.HintDto;
import com.nextroom.nextRoomServer.exceptions.CustomException;
import com.nextroom.nextRoomServer.repository.HintRepository;
import com.nextroom.nextRoomServer.repository.ThemeRepository;
import com.nextroom.nextRoomServer.security.SecurityUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HintService {
    private final S3Component s3Component;

    private final HintRepository hintRepository;
    private final ThemeRepository themeRepository;

    private final static String TYPE_HINT = "hint";
    private final static String TYPE_ANSWER = "answer";

    @Transactional
    public HintDto.UrlResponse getPresignedUrl(HintDto.UrlRequest request) {
        Long shopId = SecurityUtil.getCurrentShopId();
        Long themeId = request.getThemeId();

        validateThemeAndShop(shopId, themeId);

        List<String> hintImageUrlList = generatePresignedUrls(shopId, themeId, TYPE_HINT, request.getHintImageCount());
        List<String> answerImageUrlList = generatePresignedUrls(shopId, themeId, TYPE_ANSWER, request.getAnswerImageCount());

        return new HintDto.UrlResponse(hintImageUrlList, answerImageUrlList);
    }

    @Transactional
    public void addHint(HintDto.AddHintRequest request) {
        Theme theme = themeRepository.findById(
                request.getThemeId()) // TODO optimize by making method get theme from shop
            .orElseThrow(() -> new CustomException(THEME_NOT_FOUND));

        if (!Objects.equals(theme.getShop().getId(), SecurityUtil.getCurrentShopId())) {
            throw new CustomException(NOT_PERMITTED);
        }

        if (hintRepository.existsByThemeAndHintCode(theme, request.getHintCode())) {
            throw new CustomException(HINT_CODE_CONFLICT);
        }

        Hint hint = Hint.builder()
            .theme(theme)
            .hintCode(request.getHintCode())
            .contents(request.getContents())
            .answer(request.getAnswer())
            .progress(request.getProgress())
            .build();

        hintRepository.save(hint);
    }

    @Transactional(readOnly = true)
    public List<HintDto.HintListResponse> getHintList(Long themeId) {
        Theme theme = themeRepository.findById(
                themeId) // TODO optimize by making method get theme from shop
            .orElseThrow(() -> new CustomException(THEME_NOT_FOUND));

        if (!Objects.equals(theme.getShop().getId(), SecurityUtil.getCurrentShopId())) {
            throw new CustomException(NOT_PERMITTED);
        }

        return theme.getHints().stream().map(HintDto.HintListResponse::new).toList();
    }

    @Transactional
    public void editHint(HintDto.EditHintRequest request) {
        Hint hint = hintRepository.findById(request.getId()).orElseThrow(
            () -> new CustomException(TARGET_HINT_NOT_FOUND)
        );

        if (!Objects.equals(hint.getTheme().getShop().getId(), SecurityUtil.getCurrentShopId())) {
            throw new CustomException(NOT_PERMITTED);
        }

        hint.update(request);
    }

    @Transactional
    public void removeHint(HintDto.RemoveHintRequest request) {
        Hint hint = hintRepository.findById(request.getId()).orElseThrow(
            () -> new CustomException(HINT_NOT_FOUND)
        );

        if (!Objects.equals(hint.getTheme().getShop().getId(), SecurityUtil.getCurrentShopId())) {
            throw new CustomException(NOT_PERMITTED);
        }

        hintRepository.delete(hint);
    }

    private void validateThemeAndShop(Long shopId, Long themeId) {
        Theme theme = themeRepository.findById(themeId)
                .orElseThrow(() -> new CustomException(THEME_NOT_FOUND));

        if (!Objects.equals(theme.getShop().getId(), shopId)) {
            throw new CustomException(NOT_PERMITTED);
        }
    }

    private List<String> generatePresignedUrls(Long shopId, Long themeId, String type, int imageCount) {
        List<String> imageUrlList = new ArrayList<>();
        for (int i = 1; i <= imageCount; i++) {
            String fileName = s3Component.createFileName(shopId, themeId, type, i);
            imageUrlList.add(s3Component.createPresignedUrl(fileName));
        }
        return imageUrlList;
    }
}