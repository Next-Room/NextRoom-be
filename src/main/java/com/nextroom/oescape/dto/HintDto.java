package com.nextroom.oescape.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

public class HintDto {

    @Getter
    @RequiredArgsConstructor
    @NoArgsConstructor(force = true)
    public static class AddHintRequest {
        private final Long themeId;
        private final String hintCode;
        private final String contents;
        private final String answer;
        private final int progress;
    }

    @Getter
    @Builder
    public static class HintListResponse {
        private final Long id;
        private final String hintCode;
        private final String contents;
        private final String answer;
        private final Integer progress;
        private final String createdAt;
        private final String modifiedAt;
    }

    @Getter
    @RequiredArgsConstructor
    @NoArgsConstructor(force = true)
    public static class EditHintRequest {
        private final Long id;
        private final String hintCode;
        private final String contents;
        private final String answer;
        private final Integer progress;
    }

    @Getter
    @NoArgsConstructor
    public static class RemoveHintRequest {
        private Long id;
    }
}