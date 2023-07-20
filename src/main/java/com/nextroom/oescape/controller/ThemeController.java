package com.nextroom.oescape.controller;

import static com.nextroom.oescape.exceptions.StatusCode.*;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nextroom.oescape.dto.BaseResponse;
import com.nextroom.oescape.dto.DataResponse;
import com.nextroom.oescape.dto.ThemeDto;
import com.nextroom.oescape.service.ThemeService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Theme")
@RestController
@RequestMapping("/api/v1/theme")
@RequiredArgsConstructor
public class ThemeController {
    private final ThemeService themeService;

    @Operation(
        summary = "테마 등록",
        responses = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "404", description = "THEME_NOT_FOUND")
        }
    )
    @PostMapping
    public ResponseEntity<BaseResponse> addTheme(@RequestBody ThemeDto.AddThemeRequest request) {
        themeService.addTheme(request);
        return ResponseEntity.ok(new BaseResponse(OK));
    }

    @Operation(
        summary = "테마 조회",
        responses = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "404", description = "THEME_NOT_FOUND")
        }
    )
    @GetMapping
    public ResponseEntity<BaseResponse> getThemeList() {
        return ResponseEntity.ok(new DataResponse<>(OK, themeService.getThemeList()));
    }

    @Operation(
        summary = "테마 수정",
        responses = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "404", description = "THEME_NOT_FOUND")
        }
    )
    @PutMapping
    public ResponseEntity<BaseResponse> editTheme(@RequestBody ThemeDto.EditThemeRequest request) {
        themeService.editTheme(request);
        return ResponseEntity.ok(new BaseResponse(OK));
    }

    @Operation(
        summary = "테마 삭제",
        responses = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "404", description = "THEME_NOT_FOUND")
        }
    )
    @DeleteMapping
    public ResponseEntity<BaseResponse> removeTheme(@RequestBody ThemeDto.RemoveThemeRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        themeService.removeTheme(request);
        return ResponseEntity.ok(new BaseResponse(OK));
    }
}
