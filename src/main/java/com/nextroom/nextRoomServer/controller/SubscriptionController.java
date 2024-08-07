//package com.nextroom.nextRoomServer.controller;
//
//import static com.nextroom.nextRoomServer.exceptions.StatusCode.*;
//
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import com.nextroom.nextRoomServer.dto.BaseResponse;
//import com.nextroom.nextRoomServer.dto.DataResponse;
//import com.nextroom.nextRoomServer.dto.SubscriptionDto;
//import com.nextroom.nextRoomServer.service.SubscriptionService;
//import com.nextroom.nextRoomServer.util.Base64Decoder;
//
//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.responses.ApiResponse;
//import io.swagger.v3.oas.annotations.tags.Tag;
//import lombok.RequiredArgsConstructor;
//
//@Tag(name = "Subscription")
//@RestController
//@RequestMapping("/api/v1/subscription")
//@RequiredArgsConstructor
//public class SubscriptionController {
//    private final SubscriptionService subscriptionService;
//
//    @PostMapping
//    public ResponseEntity<BaseResponse> updateSubscription(@RequestBody SubscriptionDto.UpdateSubscription messageDto) {
//        String decodedData = Base64Decoder.decode(messageDto.getMessage().getData());
//
//        System.out.println(decodedData);
//
//        Pattern pattern = Pattern.compile(".*\"testNotification\":\\{(.*?)\\}.*");
//        Matcher matcher = pattern.matcher(decodedData);
//
//        if (matcher.find()) {
//            String notificationContent = matcher.group(1);
//            System.out.println("Extracted Notification Content: " + notificationContent);
//        }
//
//        return ResponseEntity.ok(new BaseResponse(OK));
//    }
//
//    @PostMapping("/test")
//    public ResponseEntity<BaseResponse> addSubscription() {
//        subscriptionService.test();
//        return ResponseEntity.ok(new BaseResponse(OK));
//    }
//
//    @Operation(
//        summary = "구독 정보 조회(마이 페이지)",
//        responses = {
//            @ApiResponse(responseCode = "200", description = "OK"),
//            @ApiResponse(responseCode = "401", description = "TOKEN_UNAUTHORIZED"),
//            @ApiResponse(responseCode = "403", description = "NOT_PERMITTED"),
//            @ApiResponse(responseCode = "404", description = "TARGET_SHOP_NOT_FOUND")
//        }
//    )
//    @GetMapping("/mypage")
//    public ResponseEntity<BaseResponse> getSubscriptionInfo() {
//        return ResponseEntity.ok(new DataResponse<>(OK, subscriptionService.getSubscriptionInfo()));
//    }
//
//    @Operation(
//        summary = "유저 상태 조회",
//        responses = {
//            @ApiResponse(responseCode = "200", description = "OK"),
//            @ApiResponse(responseCode = "401", description = "TOKEN_UNAUTHORIZED"),
//            @ApiResponse(responseCode = "403", description = "NOT_PERMITTED"),
//            @ApiResponse(responseCode = "404", description = "TARGET_SHOP_NOT_FOUND")
//        }
//    )
//    @GetMapping("/status")
//    public ResponseEntity<BaseResponse> getUserStatus() {
//        return ResponseEntity.ok(new DataResponse<>(OK, subscriptionService.getUserStatus()));
//    }
//
//    @Operation(
//        summary = "구독 요금제 조회",
//        responses = {
//            @ApiResponse(responseCode = "200", description = "OK"),
//            @ApiResponse(responseCode = "401", description = "TOKEN_UNAUTHORIZED"),
//            @ApiResponse(responseCode = "403", description = "NOT_PERMITTED")
//        }
//    )
//    @GetMapping("/plan")
//    public ResponseEntity<BaseResponse> getSubscriptionPlan() {
//        return ResponseEntity.ok(new DataResponse<>(OK, subscriptionService.getSubscriptionPlan()));
//    }
//}
