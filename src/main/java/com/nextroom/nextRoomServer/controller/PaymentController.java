package com.nextroom.nextRoomServer.controller;

import static com.nextroom.nextRoomServer.exceptions.StatusCode.*;

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

    @PostMapping("/purchase")
    public ResponseEntity<BaseResponse> purchaseSubscription(
        @RequestBody SubscriptionDto.PurchaseSubscription request
    ) {
        subscriptionService.purchaseSubscription(request.getPurchaseToken());
        return ResponseEntity.ok(new BaseResponse(OK));
    }

    @GetMapping
    public ResponseEntity<BaseResponse> getPaymentList() {
        return ResponseEntity.ok(new DataResponse<>(OK, subscriptionService.getShopPaymentList()));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<BaseResponse> purchaseSubscription(@PathVariable String orderId) {
        return ResponseEntity.ok(new DataResponse<>(OK, subscriptionService.getPaymentDetail(orderId)));
    }

    @PostMapping("/rtdn")
    public ResponseEntity<BaseResponse> updateSubscriptionPurchase(
        @RequestBody SubscriptionDto.UpdateSubscription requestBody) {
        subscriptionService.updateSubscription(requestBody);
        return ResponseEntity.ok(new BaseResponse(OK));
    }
}