package com.nextroom.oescape.dto;

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

}