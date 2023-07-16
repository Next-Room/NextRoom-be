package com.nextroom.oescape.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.nextroom.oescape.domain.Shop;
import com.nextroom.oescape.domain.Theme;
import com.nextroom.oescape.dto.ThemeDto;
import com.nextroom.oescape.exceptions.CustomException;
import com.nextroom.oescape.exceptions.StatusCode;
import com.nextroom.oescape.repository.ThemeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ThemeService {
	private final ThemeRepository themeRepository;

	public ThemeDto.AddThemeResponse addTheme(ThemeDto.AddThemeRequest request) {
		Theme result = themeRepository.findByTitle(request.getTitle()).orElseThrow(
			() -> new CustomException(StatusCode.BAD_REQUEST)
		);

		Theme theme = Theme.builder()
			.title(request.getTitle())
			.timeLimit(request.getTimeLimit())
			.build();

		Theme savedTheme = themeRepository.save(theme);

		return ThemeDto.AddThemeResponse.builder()
			.id(savedTheme.getId())
			.title(savedTheme.getTitle())
			.timeLimit(savedTheme.getTimeLimit())
			.build();
	}

	public List<ThemeDto.ThemeListResponse> getThemeList() {
		//TODO 회원 검증 로직

		List<Theme> themeList = themeRepository.findAllByShop(new Shop());
		List<ThemeDto.ThemeListResponse> themeListResponses = new ArrayList<>();
		for (Theme theme : themeList) {
			themeListResponses.add(ThemeDto.ThemeListResponse
				.builder()
				.id(theme.getId())
				.title(theme.getTitle())
				.timeLimit(theme.getTimeLimit())
				.build());
		}
		return themeListResponses;
	}

	public void editTheme(ThemeDto.EditThemeRequest request) {
		//TODO 테마 사용자 조회

		//TODO 테마 조회
		Theme theme = themeRepository.findById(request.getId()).orElseThrow(
			() -> new CustomException(StatusCode.BAD_REQUEST)
		);

		//TODO 테마 수정
	}

	public void removeTheme(ThemeDto.RemoveRequest request) {
		//TODO 테마 사용자 조회

		//TODO 테마 조회

		//TODO 테마 삭제
	}
}
