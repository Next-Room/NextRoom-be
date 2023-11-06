package com.nextroom.nextRoomServer.controller;

import static com.nextroom.nextRoomServer.exceptions.StatusCode.*;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nextroom.nextRoomServer.dto.BaseResponse;
import com.nextroom.nextRoomServer.dto.DataResponse;
import com.nextroom.nextRoomServer.dto.ThemeDto;
import com.nextroom.nextRoomServer.service.ThemeService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
            @ApiResponse(responseCode = "401", description = "TOKEN_UNAUTHORIZED"),
            @ApiResponse(responseCode = "403", description = "NOT_PERMITTED"),
            @ApiResponse(responseCode = "404", description = "TARGET_SHOP_NOT_FOUND"),
            @ApiResponse(responseCode = "404", description = "TARGET_THEME_NOT_FOUND")
        }
    )
    @PostMapping
    public ResponseEntity<BaseResponse> addTheme(@RequestBody @Valid ThemeDto.AddThemeRequest request) {
        themeService.addTheme(request);
        return ResponseEntity.ok(new BaseResponse(OK));
    }

    @Operation(
        summary = "테마 조회",
        responses = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "401", description = "TOKEN_UNAUTHORIZED"),
            @ApiResponse(responseCode = "403", description = "NOT_PERMITTED"),
            @ApiResponse(responseCode = "404", description = "TARGET_SHOP_NOT_FOUND"),
            @ApiResponse(responseCode = "404", description = "TARGET_THEME_NOT_FOUND")
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
            @ApiResponse(responseCode = "401", description = "TOKEN_UNAUTHORIZED"),
            @ApiResponse(responseCode = "403", description = "NOT_PERMITTED"),
            @ApiResponse(responseCode = "404", description = "TARGET_SHOP_NOT_FOUND"),
            @ApiResponse(responseCode = "404", description = "TARGET_THEME_NOT_FOUND")
        }
    )
    @PutMapping
    public ResponseEntity<BaseResponse> editTheme(@RequestBody @Valid ThemeDto.EditThemeRequest request) {
        themeService.editTheme(request);
        return ResponseEntity.ok(new BaseResponse(OK));
    }

    @Operation(
        summary = "테마 삭제",
        responses = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "401", description = "TOKEN_UNAUTHORIZED"),
            @ApiResponse(responseCode = "403", description = "NOT_PERMITTED"),
            @ApiResponse(responseCode = "404", description = "TARGET_SHOP_NOT_FOUND"),
            @ApiResponse(responseCode = "404", description = "TARGET_THEME_NOT_FOUND")
        }
    )
    @DeleteMapping
    public ResponseEntity<BaseResponse> removeTheme(@RequestBody @Valid ThemeDto.RemoveThemeRequest request) {
        themeService.removeTheme(request);
        return ResponseEntity.ok(new BaseResponse(OK));
    }
}
