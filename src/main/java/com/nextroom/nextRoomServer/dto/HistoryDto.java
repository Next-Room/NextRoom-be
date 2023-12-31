//package com.nextroom.nextRoomServer.dto;
//
//import java.time.LocalDateTime;
//import java.util.List;
//
//import com.fasterxml.jackson.annotation.JsonFormat;
//
//import lombok.Builder;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import lombok.RequiredArgsConstructor;
//
//public class HistoryDto {
//    @Getter
//    @RequiredArgsConstructor
//    @NoArgsConstructor(force = true)
//    public static class AddPlayHistoryRequest {
//        private final Long themeId;
//        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
//        private final LocalDateTime gameStartTime;
//        private final List<AddHintHistoryRequest> hint;
//    }
//
//    @Getter
//    @RequiredArgsConstructor
//    @NoArgsConstructor(force = true)
//    public static class AddHintHistoryRequest {
//        private final Long id;
//        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
//        private final LocalDateTime entryTime;
//        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
//        private final LocalDateTime answerOpenTime;
//    }
//
//    @Getter
//    @Builder
//    @RequiredArgsConstructor
//    @NoArgsConstructor(force = true)
//    public static class ThemeAnalyticsResponse {
//        private final Long themeId;
//        private final Integer totalPlayCount;
//        private final List<HintAnalyticsListResponse> hint;
//    }
//
//    @Getter
//    @Builder
//    @RequiredArgsConstructor
//    @NoArgsConstructor(force = true)
//    public static class HintAnalyticsListResponse {
//        private final Long id;
//        private final Integer hintOpenCount;
//        private final Integer answerOpenCount;
//    }
//}
