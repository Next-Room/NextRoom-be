package com.nextroom.oescape.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

public class HintDto {

    @Getter
    @RequiredArgsConstructor
    @NoArgsConstructor(force = true)
    public static class AddHintRequest {
        @NotBlank(message = "테마 ID를 입력해 주세요.")
        private final Long themeId;
        @NotBlank(message = "힌트 제목을 입력해 주세요.")
        private final String hintTitle;
        @NotBlank(message = "힌트 코드를 입력해 주세요.")
        private final String hintCode;
        @NotBlank(message = "힌트 내용을 입력해 주세요.")
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
        @NotBlank(message = "힌트 ID를 입력해 주세요.")
        private final Long id;
        @NotBlank(message = "힌트 제목을 입력해 주세요.")
        private final String hintTitle;
        @NotBlank(message = "힌트 코드를 입력해 주세요.")
        private final String hintCode;
        @NotBlank(message = "힌트 내용을 입력해 주세요.")
        private final String contents;
        private final String answer;
        private final Integer progress;
    }

    @Getter
    @NoArgsConstructor
    public static class RemoveHintRequest {
        @NotBlank(message = "힌트 ID를 입력해 주세요.")
        private Long id;
    }
}