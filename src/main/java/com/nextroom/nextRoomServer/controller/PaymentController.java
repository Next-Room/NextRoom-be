package com.nextroom.nextRoomServer.controller;

import static com.nextroom.nextRoomServer.exceptions.StatusCode.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nextroom.nextRoomServer.dto.BaseResponse;
import com.nextroom.nextRoomServer.dto.DataResponse;
import com.nextroom.nextRoomServer.dto.SubscriptionDto;
import com.nextroom.nextRoomServer.service.SubscriptionService;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Payment")
@RestController
@RequestMapping("/api/v1/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final SubscriptionService subscriptionService;

    @Operation(
            summary = "구독 결제",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "401", description = "TOKEN_UNAUTHORIZED", content = @Content),
                    @ApiResponse(responseCode = "403", description = "NOT_PERMITTED", content = @Content),
                    @ApiResponse(responseCode = "404", description = "TARGET_SHOP_NOT_FOUND", content = @Content),
                    @ApiResponse(responseCode = "500", description = "INTERNAL_SERVER_ERROR", content = @Content)
            }

    )
    @PostMapping("/purchase")
    public ResponseEntity<BaseResponse> purchaseSubscription(
        @RequestBody SubscriptionDto.PurchaseSubscription request
    ) {
        subscriptionService.purchaseSubscription(request.getPurchaseToken());
        return ResponseEntity.ok(new BaseResponse(OK));
    }

    @Operation(hidden = true)
    @GetMapping
    public ResponseEntity<BaseResponse> getPaymentList() {
        return ResponseEntity.ok(new DataResponse<>(OK, subscriptionService.getShopPaymentList()));
    }

    @Operation(hidden = true)
    @GetMapping("/{orderId}")
    public ResponseEntity<BaseResponse> purchaseSubscription(@PathVariable String orderId) {
        return ResponseEntity.ok(new DataResponse<>(OK, subscriptionService.getPaymentDetail(orderId)));
    }

    @Operation(hidden = true)
    @PostMapping("/rtdn")
    public ResponseEntity<BaseResponse> updateSubscriptionPurchase(
        @RequestBody SubscriptionDto.UpdateSubscription requestBody) {
        subscriptionService.updateSubscription(requestBody);
        return ResponseEntity.ok(new BaseResponse(OK));
    }
}