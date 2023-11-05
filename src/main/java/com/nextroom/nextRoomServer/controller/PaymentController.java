package com.nextroom.nextRoomServer.controller;

import static com.nextroom.nextRoomServer.exceptions.StatusCode.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.services.androidpublisher.model.SubscriptionPurchase;
import com.nextroom.nextRoomServer.dto.BaseResponse;
import com.nextroom.nextRoomServer.dto.SubscriptionDto;
import com.nextroom.nextRoomServer.util.AndroidPublisherClient;
import com.nextroom.nextRoomServer.util.Base64Decoder;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Payment")
@RestController
@RequestMapping("/api/v1/payment")
@RequiredArgsConstructor
public class PaymentController {
    @PostMapping
    public ResponseEntity<BaseResponse> updatePayment(
        // @RequestBody Object message
        @RequestBody SubscriptionDto.UpdateSubscription messageDto
    ) throws
        Exception {
        System.out.println(messageDto.getMessage());

        // AndroidPublisherClient androidPublisherClient = new AndroidPublisherClient();
        // SubscriptionPurchase subscriptionPurchase = androidPublisherClient.getSubscriptionPurchase("1",
        //     "fhcdpgkjdpngkkealpkolhaf.AO-J1OzVCAYlVXjtKwF3vymo0VO1x2S2CZnD9lDszWLd4ePJ9hV6-QkM-zTv2_gUPNeDTPZhZWFgPiuZbEal5uX80iYaloHItHPOBJ43Kv_Tu7OZdoHijtg");

        //TEST END
        String decodedData = Base64Decoder.decode(messageDto.getMessage().getData());

        System.out.println("decodedData = " + decodedData);

        Pattern pattern = Pattern.compile(
            ".*\"subscriptionNotification\":\\{(.*?)\\}.*"); // TODO modify test -> subscription
        Matcher matcher = pattern.matcher(decodedData);

        if (matcher.find()) {
            String notificationContent = matcher.group(1);
            ObjectMapper objectMapper = new ObjectMapper();
            SubscriptionDto.PublishedMessage publishedMessage = objectMapper.readValue(decodedData,
                SubscriptionDto.PublishedMessage.class);
            System.out.println(publishedMessage.getSubscriptionNotification().getPurchaseToken());
        }

        return ResponseEntity.ok(new BaseResponse(OK));
    }
}
