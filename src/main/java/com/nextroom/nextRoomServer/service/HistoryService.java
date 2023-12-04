package com.nextroom.nextRoomServer.service;

import static com.nextroom.nextRoomServer.exceptions.StatusCode.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nextroom.nextRoomServer.domain.Hint;
import com.nextroom.nextRoomServer.domain.HintHistory;
import com.nextroom.nextRoomServer.domain.PlayHistory;
import com.nextroom.nextRoomServer.domain.Theme;
import com.nextroom.nextRoomServer.dto.HistoryDto;
import com.nextroom.nextRoomServer.exceptions.CustomException;
import com.nextroom.nextRoomServer.repository.HintHistoryRepository;
import com.nextroom.nextRoomServer.repository.HintRepository;
import com.nextroom.nextRoomServer.repository.PlayHistoryRepository;
import com.nextroom.nextRoomServer.repository.ThemeRepository;
import com.nextroom.nextRoomServer.security.SecurityUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HistoryService {
    private final PlayHistoryRepository playHistoryRepository;
    private final HintHistoryRepository hintHistoryRepository;
    private final ThemeRepository themeRepository;
    private final HintRepository hintRepository;

    @Transactional
    public void addHistory(HistoryDto.AddPlayHistoryRequest request) {
        Theme theme = themeRepository.findById(request.getThemeId())
            .orElseThrow(() -> new CustomException(TARGET_THEME_NOT_FOUND));

        if (!Objects.equals(theme.getShop().getId(), SecurityUtil.getRequestedShopId())) {
            throw new CustomException(NOT_PERMITTED);
        }

        PlayHistory playHistory = PlayHistory.builder()
            .theme(theme)
            .gameStartTime(request.getGameStartTime())
            .build();

        playHistory = playHistoryRepository.saveAndFlush(playHistory);

        for (HistoryDto.AddHintHistoryRequest hintHistoryRequest : request.getHint()) {
            Hint hint = hintRepository.findById(hintHistoryRequest.getId()).orElseThrow(
                () -> new CustomException(TARGET_HINT_NOT_FOUND));

            HintHistory hintHistory = HintHistory.builder()
                .playHistory(playHistory)
                .hint(hint)
                .entryTime(hintHistoryRequest.getEntryTime())
                .answerOpenTime(hintHistoryRequest.getAnswerOpenTime())
                .build();

            hintHistoryRepository.save(hintHistory);
        }
    }

    @Transactional(readOnly = true)
    public HistoryDto.ThemeAnalyticsResponse getThemeAnalytics(Long themeId) {
        Theme theme = themeRepository.findById(themeId)
            .orElseThrow(() -> new CustomException(TARGET_THEME_NOT_FOUND));

        if (!Objects.equals(theme.getShop().getId(), SecurityUtil.getRequestedShopId())) {
            throw new CustomException(NOT_PERMITTED);
        }

        List<Object[]> hintAnalyticsList = hintHistoryRepository.findAnalyticsByThemeId(
            theme.getId());

        List<HistoryDto.HintAnalyticsListResponse> hintAnalyticsListResponses = new ArrayList<>();
        for (Object[] objects : hintAnalyticsList) {
            Long hintId = (Long)objects[0];
            Long hintOpenCount = (Long)objects[1];
            BigDecimal answerCount = (BigDecimal)objects[2];

            Integer convertedHintOpenCount = hintOpenCount != null ? hintOpenCount.intValue() : 0;
            Integer convertedAnswerCount = answerCount != null ? answerCount.intValue() : 0;

            HistoryDto.HintAnalyticsListResponse hintAnalyticsDto = HistoryDto.HintAnalyticsListResponse.builder()
                .id(hintId)
                .hintOpenCount(convertedHintOpenCount)
                .answerOpenCount(convertedAnswerCount)
                .build();
            hintAnalyticsListResponses.add(hintAnalyticsDto);
        }

        Integer totalPlayCount = playHistoryRepository.countByThemeId(theme.getId());

        return HistoryDto.ThemeAnalyticsResponse.builder()
            .themeId(theme.getId())
            .totalPlayCount(totalPlayCount)
            .hint(hintAnalyticsListResponses)
            .build();
    }
}
