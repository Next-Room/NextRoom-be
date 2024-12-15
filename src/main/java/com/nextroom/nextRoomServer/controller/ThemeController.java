package com.nextroom.nextRoomServer.controller;

import static com.nextroom.nextRoomServer.exceptions.StatusCode.*;

import com.nextroom.nextRoomServer.dto.ThemeDto.ThemeUrlResponse;
import io.swagger.v3.oas.annotations.media.Content;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
            @ApiResponse(responseCode = "401", description = "TOKEN_UNAUTHORIZED", content = @Content),
            @ApiResponse(responseCode = "403", description = "NOT_PERMITTED", content = @Content),
            @ApiResponse(responseCode = "404", description = "TARGET_SHOP_NOT_FOUND", content = @Content),
            @ApiResponse(responseCode = "404", description = "TARGET_THEME_NOT_FOUND", content = @Content)
        }
    )
    @PostMapping
    public ResponseEntity<DataResponse<ThemeDto.AddThemeResponse>> addTheme(@RequestBody @Valid ThemeDto.AddThemeRequest request) {
        return ResponseEntity.ok(new DataResponse<>(OK, themeService.addTheme(request)));
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

    @Operation(
            summary = "테마 타이머 배경 이미지 PreSigned Url 요청",
            description = """
                    s3 url /{profile}/{shopId}/{themeId}/{type}/{num}_uuid.png

                    ex) "/dev/1/3/timer/1_2e20b6a9-e24b-45a8-a974-005c14f9f44f.png"
                    """,
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "401", description = "TOKEN_UNAUTHORIZED"),
                    @ApiResponse(responseCode = "403", description = "NOT_PERMITTED"),
                    @ApiResponse(responseCode = "404", description = "TARGET_THEME_NOT_FOUND")
            }
    )
    @GetMapping("/timer/url/{themeId}")
    public ResponseEntity<DataResponse<ThemeUrlResponse>> getUrl(@PathVariable Long themeId) {
        return ResponseEntity.ok(new DataResponse<>(OK, themeService.getTimerUrl(themeId)));
    }

    @Operation(
            summary = "테마 타이머 배경 이미지 추가",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "401", description = "TOKEN_UNAUTHORIZED"),
                    @ApiResponse(responseCode = "403", description = "NOT_PERMITTED"),
                    @ApiResponse(responseCode = "404", description = "TARGET_THEME_NOT_FOUND")
            }
    )
    @PostMapping("/timer")
    public ResponseEntity<BaseResponse> addThemeTimerImage(@RequestBody @Valid ThemeDto.ThemeUrlRequest request) {
        themeService.addThemeTimerImage(request);
        return ResponseEntity.ok(new BaseResponse(OK));
    }

    @Operation(
            summary = "테마 타이머 배경 이미지 삭제",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "401", description = "TOKEN_UNAUTHORIZED"),
                    @ApiResponse(responseCode = "403", description = "NOT_PERMITTED"),
                    @ApiResponse(responseCode = "404", description = "TARGET_THEME_NOT_FOUND")
            }
    )
    @DeleteMapping("/timer/{themeId}")
    public ResponseEntity<BaseResponse> removeTheme(@PathVariable Long themeId) {
        themeService.removeThemeTimerImage(themeId);
        return ResponseEntity.ok(new BaseResponse(OK));
    }

    @Operation(
            summary = "(앱) 테마 타이머 배경 on/off 설정",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "401", description = "TOKEN_UNAUTHORIZED"),
                    @ApiResponse(responseCode = "403", description = "NOT_PERMITTED"),
                    @ApiResponse(responseCode = "404", description = "TARGET_THEME_NOT_FOUND")
            }
    )
    @PutMapping("/timer/active")
    public ResponseEntity<BaseResponse> activeTimerUrl(@RequestBody @Valid ThemeDto.ThemeActiveUrlRequest request) {
        themeService.activeThemeTimerUrl(request);
        return ResponseEntity.ok(new BaseResponse(OK));
    }
}
