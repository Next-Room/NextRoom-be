package com.nextroom.nextRoomServer.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.google.gson.Gson;
import com.nextroom.nextRoomServer.dto.ThemeDto;

@ExtendWith(MockitoExtension.class)
class ThemeControllerTest {

    @InjectMocks
    private ThemeController themeController;

    private MockMvc mockMvc;
    private Gson gson;

    @BeforeEach
    void init() {
        gson = new Gson();
        mockMvc = MockMvcBuilders.standaloneSetup(themeController).build();
    }

    @Test
    @DisplayName("테마 등록 실패_사용자 식별값 헤더에 없음")
    void failAddTheme() throws Exception {
        //given
        String url = "/api/v1/theme";

        //when
        ResultActions resultActions = mockMvc.perform(
            MockMvcRequestBuilders.post(url)
                .content(gson.toJson(addThemeRequest("테마 이름", 70)))
                .contentType(MediaType.APPLICATION_JSON)
        );

        //then
        resultActions.andExpect(status().isBadRequest());
    }

    private ThemeDto.AddThemeRequest addThemeRequest(String title, int timeLimit) {
        return ThemeDto.AddThemeRequest.builder()
            .title(title)
            .timeLimit(timeLimit)
            .build();
    }
}