package com.nextroom.nextRoomServer.controller;

import static com.nextroom.nextRoomServer.exceptions.StatusCode.*;

import java.io.IOException;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nextroom.nextRoomServer.dto.BaseResponse;
import com.nextroom.nextRoomServer.dto.SubscriptionDto;
import com.nextroom.nextRoomServer.enums.SubscriptionStatus;
import com.nextroom.nextRoomServer.service.SubscriptionService;
import com.nextroom.nextRoomServer.util.Base64Decoder;

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
        @RequestBody SubscriptionDto.PurchaseSubscription requestBody
    ) throws IOException {
        subscriptionService.purchaseSubscription(requestBody.getPurchaseToken());
        return ResponseEntity.ok(new BaseResponse(OK));
    }

    @PostMapping("rtdn")
    public ResponseEntity<BaseResponse> updateSubscriptionPurchase(
        @RequestBody SubscriptionDto.updateSubscription requestBody
    ) throws
        Exception {
        String decodedData = Base64Decoder.decode(requestBody.getMessage().getData());

        Pattern pattern = Pattern.compile(
            ".*\"subscriptionNotification\":\\{(.*?)\\}.*");
        Matcher matcher = pattern.matcher(decodedData);

        String notificationContent = matcher.group(1);
        ObjectMapper objectMapper = new ObjectMapper();
        SubscriptionDto.PublishedMessage publishedMessage = objectMapper.readValue(decodedData,
            SubscriptionDto.PublishedMessage.class);
        System.out.println(publishedMessage.getSubscriptionNotification().getPurchaseToken());

        //     TODO handle exception
        Integer notificationType = publishedMessage.getSubscriptionNotification().getNotificationType();
        String purchaseToken = publishedMessage.getSubscriptionNotification().getPurchaseToken();

        if (Objects.equals(notificationType, SubscriptionStatus.SUBSCRIPTION_RENEWED.getStatus())) {
            subscriptionService.renew(purchaseToken);
        } else if (Objects.equals(notificationType, SubscriptionStatus.SUBSCRIPTION_EXPIRED.getStatus())) {
            subscriptionService.expire(purchaseToken);
        }
        return ResponseEntity.ok(new BaseResponse(OK));
    }
}