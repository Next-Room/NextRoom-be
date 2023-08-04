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
import org.springframework.boot.test.context.SpringBootTest;

import com.nextroom.oescape.domain.Hint;
import com.nextroom.oescape.dto.HintDto;
import com.nextroom.oescape.repository.HintRepository;
import com.nextroom.oescape.repository.ThemeRepository;

@SpringBootTest
public class HintServiceTest {

    @InjectMocks
    private HintService hintService;

    @Mock
    private HintRepository hintRepository;

    @Mock
    private ThemeRepository themeRepository;

    @Test
    @DisplayName("힌트 추가 / 성공")
    public void testAddHint() {
        HintDto.AddHintRequest request = new HintDto.AddHintRequest();
        // Set request parameters here

        hintService.addHint(request);

        verify(hintRepository, times(1)).save(any(Hint.class));
    }

    @Test
    @DisplayName("힌트 조회 / 성공")
    public void testGetHintList() {
        Long themeId = 1L;
        when(hintRepository.findAllByThemeId(themeId)).thenReturn(Collections.emptyList());

        List<HintDto.HintListResponse> hints = hintService.getHintList(themeId);

        assertEquals(0, hints.size());
        verify(hintRepository, times(1)).findAllByThemeId(themeId);
    }

    @Test
    @DisplayName("힌트 수정 / 성공")
    public void testEditHint() {
        HintDto.EditHintRequest request = new HintDto.EditHintRequest();
        // Set request parameters here

        Hint existingHint = new Hint();
        // Set existing hint properties here

        when(hintRepository.findById(request.getId())).thenReturn(Optional.of(existingHint));

        hintService.editHint(request);

        verify(hintRepository, times(1)).save(existingHint);
    }

    @Test
    @DisplayName("힌트 삭제 / 성공")
    public void testRemoveHint() {
        HintDto.RemoveHintRequest request = new HintDto.RemoveHintRequest();
        // Set request parameters here

        Hint existingHint = new Hint();
        // Set existing hint properties here

        when(hintRepository.findById(request.getId())).thenReturn(Optional.of(existingHint));

        hintService.removeHint(request);

        verify(hintRepository, times(1)).delete(existingHint);
    }
}
