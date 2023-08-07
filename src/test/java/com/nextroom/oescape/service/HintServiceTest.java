package com.nextroom.oescape.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;

import com.nextroom.oescape.domain.Hint;
import com.nextroom.oescape.domain.Shop;
import com.nextroom.oescape.domain.Theme;
import com.nextroom.oescape.dto.HintDto;
import com.nextroom.oescape.repository.HintRepository;
import com.nextroom.oescape.repository.ThemeRepository;
import com.nextroom.oescape.security.SecurityUtil;

@SpringBootTest
public class HintServiceTest {

    @InjectMocks
    private HintService hintService;

    @Mock
    private HintRepository hintRepository;

    @Mock
    private ThemeRepository themeRepository;

    @Test
    @WithMockUser
    @DisplayName("힌트 추가 / 성공")
    public void testAddHint() {
        Long shopId = 12L;
        Shop shop = Shop.builder()
            .id(shopId).
            build();

        Long themeId = 1L;
        Theme theme = Theme.builder()
            .id(themeId)
            .hintLimit(99)
            .shop(shop)
            .build();

        HintDto.AddHintRequest request = new HintDto.AddHintRequest(
            themeId,
            "1234",
            "contents",
            "answer",
            23
        );

        when(themeRepository.findById(themeId)).thenReturn(Optional.ofNullable(theme));

        try (MockedStatic<SecurityUtil> mocked = mockStatic(SecurityUtil.class)) {
            mocked.when(SecurityUtil::getRequestedShopId).thenReturn(shopId);
            hintService.addHint(request);
        }

        assertNotNull(theme);
        verify(hintRepository, times(1)).save(any(Hint.class));
    }

    @Test
    @WithMockUser
    @DisplayName("힌트 조회 / 성공")
    public void testGetHintList() {
        Long shopId = 122L;
        Shop shop = Shop.builder()
            .id(shopId).
            build();

        Long themeId = 12L;
        Theme theme = Theme.builder()
            .id(themeId)
            .hintLimit(99)
            .shop(shop)
            .build();

        when(themeRepository.findById(themeId)).thenReturn(Optional.ofNullable(theme));
        when(hintRepository.findAllByThemeId(themeId)).thenReturn(Collections.emptyList());

        try (MockedStatic<SecurityUtil> mocked = mockStatic(SecurityUtil.class)) {
            mocked.when(SecurityUtil::getRequestedShopId).thenReturn(shopId);
            List<HintDto.HintListResponse> hints = hintService.getHintList(themeId);
            assertEquals(0, hints.size());
        }

    }

    @Test
    @WithMockUser
    @DisplayName("힌트 수정 / 성공")
    public void testEditHint() {
        Long shopId = 122L;
        Shop shop = Shop.builder()
            .id(shopId).
            build();

        Long themeId = 12L;
        Theme theme = Theme.builder()
            .id(themeId)
            .hintLimit(99)
            .shop(shop)
            .build();

        Long hintId = 12L;
        HintDto.EditHintRequest request = new HintDto.EditHintRequest(
            hintId,
            "1234",
            "contents",
            "answer",
            23
        );
        Hint hint = Hint.builder()
            .id(hintId)
            .hintCode("4312")
            .contents("asdf")
            .answer("qwer")
            .progress(13)
            .theme(theme)
            .build();
        when(hintRepository.findById(hintId)).thenReturn(Optional.ofNullable(hint));

        try (MockedStatic<SecurityUtil> mocked = mockStatic(SecurityUtil.class)) {
            mocked.when(SecurityUtil::getRequestedShopId).thenReturn(shopId);
            hintService.editHint(request);
        }
    }

    @Test
    @WithMockUser
    @DisplayName("힌트 삭제 / 성공")
    public void testRemoveHint() {
        Long shopId = 122L;
        Shop shop = Shop.builder()
            .id(shopId).
            build();

        Long themeId = 12L;
        Theme theme = Theme.builder()
            .id(themeId)
            .hintLimit(99)
            .shop(shop)
            .build();

        Long hintId = 12L;
        HintDto.RemoveHintRequest request = new HintDto.RemoveHintRequest();
        request.setId(hintId);
        Hint hint = Hint.builder()
            .id(hintId)
            .hintCode("4312")
            .contents("asdf")
            .answer("qwer")
            .progress(13)
            .theme(theme)
            .build();

        when(hintRepository.findById(hintId)).thenReturn(Optional.ofNullable(hint));

        try (MockedStatic<SecurityUtil> mocked = mockStatic(SecurityUtil.class)) {
            mocked.when(SecurityUtil::getRequestedShopId).thenReturn(shopId);
            hintService.removeHint(request);
        }
    }
}
