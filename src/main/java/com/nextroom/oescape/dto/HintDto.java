package com.nextroom.oescape.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

public class HintDto {

    @Getter
    @RequiredArgsConstructor
    public static class AddHintRequest {
        private final Long themeId;
        private final String hintTitle;
        private final String hintCode;
        private final String contents;
        private final String answer;
        private final int progress;
    }

    @Getter
    @Builder
    public static class HintListResponse {
        private final Long id;
        private final String hintTitle;
        private final String hintCode;
        private final String contents;
        private final String answer;
        private final Integer progress;
    }

    @Getter
    @Builder
    public static class EditHintRequest {
        private final Long id;
        private final String hintTitle;
        private final String hintCode;
        private final String contents;
        private final String answer;
        private final Integer progress;
    }
}