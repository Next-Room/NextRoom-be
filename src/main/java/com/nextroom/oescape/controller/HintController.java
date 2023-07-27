package com.nextroom.oescape.controller;

import static com.nextroom.oescape.exceptions.StatusCode.*;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nextroom.oescape.dto.BaseResponse;
import com.nextroom.oescape.dto.DataResponse;
import com.nextroom.oescape.dto.HintDto;
import com.nextroom.oescape.service.HintService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Hint")
@RestController
@RequestMapping("/api/v1/hint")
@RequiredArgsConstructor
public class HintController {
    private final HintService hintService;

    @Operation(
        summary = "힌트 등록",
        responses = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD_REQUEST"),
            @ApiResponse(responseCode = "403", description = "NOT_PERMITTED"),
            @ApiResponse(responseCode = "404", description = "HINT_NOT_FOUND"),
            @ApiResponse(responseCode = "409", description = "HINT_CODE_CONFLICT"),
        }
    )
    @PostMapping
    public ResponseEntity<BaseResponse> addHint(@RequestBody @Valid HintDto.AddHintRequest request) {
        hintService.addHint(request);
        return ResponseEntity.ok(new BaseResponse(OK));
    }

    @Operation(
        summary = "힌트 조회",
        responses = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "403", description = "NOT_PERMITTED"),
            @ApiResponse(responseCode = "404", description = "HINT_NOT_FOUND")
        }
    )
    @GetMapping
    public ResponseEntity<BaseResponse> getHintList(@RequestParam("themeId") Long themeId) {
        if (SecurityContextHolder.getContext().getAuthentication().getPrincipal() == "anonymousUser") {
            return ResponseEntity.ok(new DataResponse<>(OK, hintService.getHintListByThemeId(themeId)));
        }
        return ResponseEntity.ok(new DataResponse<>(OK, hintService.getHintList(themeId)));
    }

    @Operation(
        summary = "힌트 수정",
        responses = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "403", description = "NOT_PERMITTED"),
            @ApiResponse(responseCode = "404", description = "HINT_NOT_FOUND")
        }
    )
    @PutMapping
    public ResponseEntity<BaseResponse> editHint(@RequestBody @Valid HintDto.EditHintRequest request) {
        hintService.editHint(request);
        return ResponseEntity.ok(new BaseResponse(OK));
    }

    @Operation(
        summary = "힌트 삭제",
        responses = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "403", description = "NOT_PERMITTED"),
            @ApiResponse(responseCode = "404", description = "HINT_NOT_FOUND")
        }
    )
    @DeleteMapping
    public ResponseEntity<BaseResponse> removeHint(@RequestBody @Valid HintDto.RemoveHintRequest request) {
        hintService.removeHint(request);
        return ResponseEntity.ok(new BaseResponse(OK));
    }
}
