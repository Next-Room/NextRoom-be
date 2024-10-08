package com.nextroom.nextRoomServer.controller;

import static com.nextroom.nextRoomServer.exceptions.StatusCode.OK;

import com.nextroom.nextRoomServer.dto.DataResponse;
import com.nextroom.nextRoomServer.dto.SubscriptionDto;
import com.nextroom.nextRoomServer.service.SubscriptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Subscription")
@RestController
@RequestMapping("/api/v1/subscription")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @Operation(
        summary = "구독 정보 조회(마이 페이지)",
        responses = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "401", description = "TOKEN_UNAUTHORIZED", content = @Content),
            @ApiResponse(responseCode = "403", description = "NOT_PERMITTED", content = @Content),
            @ApiResponse(responseCode = "404", description = "TARGET_SHOP_NOT_FOUND", content = @Content)
        }
    )
    @GetMapping("/mypage")
    public ResponseEntity<DataResponse<SubscriptionDto.SubscriptionInfoResponse>> getSubscriptionInfo() {
        return ResponseEntity.ok(new DataResponse<>(OK, subscriptionService.getSubscriptionInfo()));
    }

    @Operation(
        summary = "유저 상태 조회",
        responses = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "401", description = "TOKEN_UNAUTHORIZED", content = @Content),
            @ApiResponse(responseCode = "403", description = "NOT_PERMITTED", content = @Content),
            @ApiResponse(responseCode = "404", description = "TARGET_SHOP_NOT_FOUND", content = @Content)
        }
    )
    @GetMapping("/status")
    public ResponseEntity<DataResponse<SubscriptionDto.UserStatusResponse>> getUserStatus() {
        return ResponseEntity.ok(new DataResponse<>(OK, subscriptionService.getUserStatus()));
    }

    @Operation(
        summary = "구독 요금제 조회",
        responses = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "401", description = "TOKEN_UNAUTHORIZED", content = @Content),
            @ApiResponse(responseCode = "403", description = "NOT_PERMITTED", content = @Content)
        }
    )
    @GetMapping("/plan")
    public ResponseEntity<DataResponse<SubscriptionDto.SubscriptionPlanResponse>> getSubscriptionPlan() {
        return ResponseEntity.ok(new DataResponse<>(OK, subscriptionService.getSubscriptionPlan()));
    }
}
