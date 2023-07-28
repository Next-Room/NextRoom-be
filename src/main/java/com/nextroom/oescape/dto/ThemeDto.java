package com.nextroom.oescape.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

public class ThemeDto {

    @Getter
    @Builder
    @RequiredArgsConstructor
    @NoArgsConstructor(force = true)
    public static class AddThemeRequest {
        @NotBlank(message = "테마 이름을 입력해 주세요.")
        private final String title;
        private final Integer timeLimit;
    }

    @Getter
    @Builder
    public static class ThemeListResponse {
        private final Long id;
        private final String title;
        private final Integer timeLimit;
        private final String createdAt;
        private final String modifiedAt;
    }

    @Getter
    @RequiredArgsConstructor
    @NoArgsConstructor(force = true)
    public static class EditThemeRequest {
        @Positive(message = "THEME ID를 입력해 주세요.")
        private final Long id;
        @NotBlank(message = "테마 이름을 입력해 주세요.")
        private final String title;
        private final Integer timeLimit;
    }

    @Getter
    @NoArgsConstructor
    public static class RemoveThemeRequest {
        @Positive(message = "THEME ID를 입력해 주세요.")
        private Long id;
    }
}
