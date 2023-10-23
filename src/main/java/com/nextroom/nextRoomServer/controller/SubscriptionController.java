package com.nextroom.nextRoomServer.controller;

import static com.nextroom.nextRoomServer.exceptions.StatusCode.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nextroom.nextRoomServer.dto.BaseResponse;
import com.nextroom.nextRoomServer.dto.SubscriptionDto;
import com.nextroom.nextRoomServer.util.Base64Decoder;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;


@Tag(name = "Subscription")
@RestController
@RequestMapping("/api/v1/subscription")
@RequiredArgsConstructor
public class SubscriptionController {

    @PostMapping
    public ResponseEntity<BaseResponse> updateSubscription(@RequestBody SubscriptionDto.UpdateSubscription messageDto) {
        String decodedData = Base64Decoder.decode(messageDto.getMessage().getData());

        System.out.println(decodedData);

        Pattern pattern = Pattern.compile(".*\"testNotification\":\\{(.*?)\\}.*");
        Matcher matcher = pattern.matcher(decodedData);

        if (matcher.find()) {
            String notificationContent = matcher.group(1);
            System.out.println("Extracted Notification Content: " + notificationContent);
        }

        return ResponseEntity.ok(new BaseResponse(OK));
    }
}
