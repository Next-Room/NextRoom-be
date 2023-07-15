package com.todayescape.controller;

import static com.todayescape.exceptions.StatusCode.*;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.todayescape.dto.BaseResponse;
import com.todayescape.dto.DataResponse;
import com.todayescape.dto.ThemeDto;
import com.todayescape.service.ThemeService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/theme")
@RequiredArgsConstructor
public class ThemeController {
	private final ThemeService themeService;

	@PostMapping
	public ResponseEntity<BaseResponse> addTheme(
		@RequestBody ThemeDto.AddThemeRequest request) {
		themeService.addTheme(request);
		return ResponseEntity.ok(new BaseResponse(OK));
	}

	@GetMapping
	public ResponseEntity<BaseResponse> getThemeList() {
		return ResponseEntity.ok(new DataResponse<>(OK, themeService.getThemeList()));
	}

	@PutMapping
	public ResponseEntity<BaseResponse> editTheme(
		@RequestBody ThemeDto.EditThemeRequest request) {
		themeService.editTheme(request);
		return ResponseEntity.ok(new BaseResponse(OK));
	}

	@DeleteMapping
	public ResponseEntity<BaseResponse> removeTheme(
		@RequestBody ThemeDto.RemoveRequest request) {
		themeService.removeTheme(request);
		return ResponseEntity.ok(new BaseResponse(OK));
	}
}
