package com.nextroom.nextRoomServer.service;

import static com.nextroom.nextRoomServer.exceptions.StatusCode.*;

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
        Long themeId = request.getThemeId();
        this.validateThemeAndShop(themeId);

        Long shopId = SecurityUtil.getCurrentShopId();
        List<String> hintImageUrlList = s3Component.generatePresignedUrlsForUpload(shopId, themeId, TYPE_HINT, request.getHintImageCount());
        List<String> answerImageUrlList = s3Component.generatePresignedUrlsForUpload(shopId, themeId, TYPE_ANSWER, request.getAnswerImageCount());

        return new HintDto.UrlResponse(hintImageUrlList, answerImageUrlList);
    }

    @Transactional
    public void addHint(HintDto.AddHintRequest request) {
        Theme theme = this.validateThemeAndShop(request.getThemeId());
        this.validateHintCodeConflict(theme, request.getHintCode());

        Hint hint = Hint.builder()
            .theme(theme)
            .hintCode(request.getHintCode())
            .contents(request.getContents())
            .answer(request.getAnswer())
            .progress(request.getProgress())
            .hintImageList(request.getHintImageList())
            .answerImageList(request.getAnswerImageList())
            .build();

        hintRepository.save(hint);
    }

    @Transactional(readOnly = true)
    public List<HintDto.HintListResponse> getHintList(Long themeId) {
        this.validateThemeAndShop(themeId);

        List<Hint> hints = hintRepository.findAllByThemeIdOrderByProgress(themeId);

        Long shopId = SecurityUtil.getCurrentShopId();
        return hints.stream().map(hint ->
                new HintDto.HintListResponse(
                        hint,
                        s3Component.generatePresignedUrlsForDownLoad(shopId, themeId, TYPE_HINT, hint.getHintImageList()),
                        s3Component.generatePresignedUrlsForDownLoad(shopId, themeId, TYPE_ANSWER, hint.getAnswerImageList())
                )
        ).toList();
    }

    @Transactional
    public void editHint(HintDto.EditHintRequest request) {
        Hint hint = this.validateHintAndShop(request.getId());

        deleteHintAndAnswerImages(hint);

        hint.update(request);
    }

    @Transactional
    public void removeHint(HintDto.RemoveHintRequest request) {
        Hint hint = this.validateHintAndShop(request.getId());

        deleteHintAndAnswerImages(hint);

        hintRepository.delete(hint);
    }

    private void deleteHintAndAnswerImages(Hint hint) {
        Long shopId = SecurityUtil.getCurrentShopId();
        Long themeId = hint.getTheme().getId();
        s3Component.deleteObjects(shopId, themeId, TYPE_HINT, hint.getHintImageList());
        s3Component.deleteObjects(shopId, themeId, TYPE_ANSWER, hint.getAnswerImageList());
    }

    private void validateShop(Long shopId) {
        if (!Objects.equals(shopId, SecurityUtil.getCurrentShopId())) {
            throw new CustomException(NOT_PERMITTED);
        }
    }

    private Theme validateThemeAndShop(Long themeId) {
        Theme theme = themeRepository.findById(themeId)
                .orElseThrow(() -> new CustomException(THEME_NOT_FOUND));

        this.validateShop(theme.getShop().getId());

        return theme;
    }

    private Hint validateHintAndShop(Long hintId) {
        Hint hint = hintRepository.findById(hintId)
                .orElseThrow(() -> new CustomException(HINT_NOT_FOUND));

        validateShop(hint.getTheme().getShop().getId());

        return hint;
    }

    private void validateHintCodeConflict(Theme theme, String hintCode) {
        if (hintRepository.existsByThemeAndHintCode(theme, hintCode)) {
            throw new CustomException(HINT_CODE_CONFLICT);
        }
    }
}