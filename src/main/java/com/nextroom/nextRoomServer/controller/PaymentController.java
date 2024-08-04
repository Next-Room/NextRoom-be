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
        subscriptionService.purchaseSubscription(request.getPurchaseToken(), request.getSubscriptionId());
        return ResponseEntity.ok(new BaseResponse(OK));
    }

    @GetMapping("/payment")
    public ResponseEntity<BaseResponse> getPaymentList() {
        return ResponseEntity.ok(new DataResponse<>(OK, subscriptionService.getShopPaymentList()));
    }

    @GetMapping("/payment/{orderId}")
    public ResponseEntity<BaseResponse> purchaseSubscription(@PathVariable String orderId) {
        return ResponseEntity.ok(new DataResponse<>(OK, subscriptionService.getPaymentDetail(orderId)));
    }

    @PostMapping("/rtdn")
    public ResponseEntity<BaseResponse> updateSubscriptionPurchase(
        @RequestBody SubscriptionDto.UpdateSubscription requestBody
    ) throws Exception {
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
        String subscriptionId = publishedMessage.getSubscriptionNotification().getSubscriptionId();

        if (Objects.equals(notificationType, SubscriptionStatus.SUBSCRIPTION_RENEWED.getStatus())) {
            subscriptionService.renew(purchaseToken, subscriptionId);
        } else if (Objects.equals(notificationType, SubscriptionStatus.SUBSCRIPTION_EXPIRED.getStatus())) {
            subscriptionService.expire(purchaseToken);
        }
        return ResponseEntity.ok(new BaseResponse(OK));
    }
}