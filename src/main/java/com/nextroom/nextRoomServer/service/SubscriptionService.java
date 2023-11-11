package com.nextroom.nextRoomServer.service;

import static com.nextroom.nextRoomServer.enums.SubscriptionPlan.*;
import static com.nextroom.nextRoomServer.enums.UserStatus.*;
import static com.nextroom.nextRoomServer.exceptions.StatusCode.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.nextroom.nextRoomServer.domain.Shop;
import com.nextroom.nextRoomServer.domain.Subscription;
import com.nextroom.nextRoomServer.dto.SubscriptionDto;
import com.nextroom.nextRoomServer.enums.EnumModel;
import com.nextroom.nextRoomServer.enums.SubscriptionPlan;
import com.nextroom.nextRoomServer.exceptions.CustomException;
import com.nextroom.nextRoomServer.repository.ShopRepository;
import com.nextroom.nextRoomServer.repository.SubscriptionRepository;
import com.nextroom.nextRoomServer.security.SecurityUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;
    private final ShopRepository shopRepository;

    public void test() {
        Shop shop = shopRepository.findByAdminCode("12321").orElseThrow(
            () -> new RuntimeException("ERROR"));

        Subscription entity = Subscription.builder()
            .shop(shop)
            .googleId("test")
            .status(SUBSCRIPTION)
            .plan(MINI)
            .expiryDate(LocalDate.now().plusDays(30))
            .build();
        subscriptionRepository.save(entity);
    }

    public SubscriptionDto.SubscriptionInfoResponse getSubscriptionInfo() {
        Long shopId = SecurityUtil.getRequestedShopId();
        Subscription subscription = subscriptionRepository.findByShopId(shopId).orElseThrow(
            () -> new CustomException(TARGET_SHOP_NOT_FOUND));

        return new SubscriptionDto.SubscriptionInfoResponse(subscription);
    }

    public SubscriptionDto.UserStatusResponse getUserStatus() {
        Long shopId = SecurityUtil.getRequestedShopId();
        Subscription subscription = subscriptionRepository.findByShopId(shopId).orElseThrow(
            () -> new CustomException(TARGET_SHOP_NOT_FOUND));

        return new SubscriptionDto.UserStatusResponse(subscription);
    }

    public List<SubscriptionDto.SubscriptionPlanResponse> getSubscriptionPlan() {
        return toEnumValues(SubscriptionPlan.class);
    }

    private List<SubscriptionDto.SubscriptionPlanResponse> toEnumValues(Class<? extends EnumModel> e) {
        return Arrays
            .stream(e.getEnumConstants())
            .map(SubscriptionDto.SubscriptionPlanResponse::new)
            .collect(Collectors.toList());
    }
}
