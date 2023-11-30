package com.nextroom.nextRoomServer.controller;

import static com.nextroom.nextRoomServer.exceptions.StatusCode.*;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nextroom.nextRoomServer.dto.BaseResponse;
import com.nextroom.nextRoomServer.dto.HistoryDto;
import com.nextroom.nextRoomServer.service.HistoryService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "History")
@RestController
@RequestMapping("/api/v1/history")
@RequiredArgsConstructor
public class HistoryController {
    private final HistoryService historyService;

    @Operation(
        summary = "히스토리 기록",
        responses = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "401", description = "TOKEN_UNAUTHORIZED"),
            @ApiResponse(responseCode = "403", description = "NOT_PERMITTED"),
            @ApiResponse(responseCode = "404", description = "TARGET_SHOP_NOT_FOUND"),
            @ApiResponse(responseCode = "404", description = "TARGET_THEME_NOT_FOUND")
        }
    )
    @PostMapping
    public ResponseEntity<BaseResponse> addHistory(@RequestBody HistoryDto.AddPlayHistoryRequest request) {
        historyService.addHistory(request);
        return ResponseEntity.ok(new BaseResponse(OK));
    }
}
