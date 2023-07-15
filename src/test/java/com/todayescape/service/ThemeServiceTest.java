package com.todayescape.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.todayescape.domain.Theme;
import com.todayescape.dto.ThemeDto;
import com.todayescape.exceptions.CustomException;
import com.todayescape.exceptions.StatusCode;
import com.todayescape.repository.ThemeRepository;

@ExtendWith(MockitoExtension.class)
class ThemeServiceTest {

	@InjectMocks
	private ThemeService themeService;
	@Mock
	private ThemeRepository themeRepository;

	private final String title = "테마 이름";
	private final Integer timeLimit = 70;

	@Test
	@DisplayName("테마 등록 성공")
	void addTheme() {
		//given
		doReturn(null).when(themeRepository).findByTitle(title);
		doReturn(theme()).when(themeRepository).save(any(Theme.class));

		//when
		ThemeDto.AddThemeResponse result = themeService.addTheme(
			ThemeDto.AddThemeRequest
				.builder()
				.title(title)
				.timeLimit(timeLimit)
				.build());

		//then
		assertThat(result.getTitle()).isEqualTo(title);
		assertThat(result.getTimeLimit()).isEqualTo(timeLimit);

		//verify
		verify(themeRepository, times(1)).findByTitle(title);
		verify(themeRepository, times(1)).save(any(Theme.class));
	}

	private Theme theme() {
		return Theme.builder()
			.title(title)
			.timeLimit(timeLimit)
			.build();
	}

	@Test
	@DisplayName("테마 이름 중복")
	void duplicateTheme() {
		//given
		doReturn(Theme.builder().build()).when(themeRepository).findByTitle(title);

		//when
		CustomException result = assertThrows(CustomException.class, () -> themeService.addTheme(
			ThemeDto.AddThemeRequest
				.builder()
				.title(title)
				.timeLimit(timeLimit)
				.build()));

		//then
		assertThat(result.getStatusCode()).isEqualTo(StatusCode.BAD_REQUEST);
	}
}