package com.nextroom.nextRoomServer.service;

import static com.nextroom.nextRoomServer.enums.SubscriptionPlan.*;
import static com.nextroom.nextRoomServer.enums.UserStatus.*;
import static com.nextroom.nextRoomServer.exceptions.StatusCode.*;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.google.api.services.androidpublisher.model.SubscriptionPurchaseV2;
import com.nextroom.nextRoomServer.domain.Shop;
import com.nextroom.nextRoomServer.domain.Subscription;
import com.nextroom.nextRoomServer.dto.SubscriptionDto;
import com.nextroom.nextRoomServer.enums.EnumModel;
import com.nextroom.nextRoomServer.enums.SubscriptionPlan;
import com.nextroom.nextRoomServer.exceptions.CustomException;
import com.nextroom.nextRoomServer.repository.ShopRepository;
import com.nextroom.nextRoomServer.repository.SubscriptionRepository;
import com.nextroom.nextRoomServer.security.SecurityUtil;
import com.nextroom.nextRoomServer.util.AndroidPublisherClient;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;
    private final ShopRepository shopRepository;
    private AndroidPublisherClient androidPublisherClient;

    {
        try {
            androidPublisherClient = new AndroidPublisherClient();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void test() {
        Shop shop = shopRepository.findByAdminCode("12321").orElseThrow(
            () -> new RuntimeException("ERROR"));

        Subscription entity = Subscription.builder()
            .shop(shop)
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

    public Map<String, List<SubscriptionDto.SubscriptionPlanResponse>> getSubscriptionPlan() {
        Map<String, List<SubscriptionDto.SubscriptionPlanResponse>> enumValues = new LinkedHashMap<>();
        enumValues.put("SubscriptionPlan", toEnumValues(SubscriptionPlan.class));

        return enumValues;
    }

    private List<SubscriptionDto.SubscriptionPlanResponse> toEnumValues(Class<? extends EnumModel> e) {
        return Arrays
            .stream(e.getEnumConstants())
            .map(SubscriptionDto.SubscriptionPlanResponse::new)
            .collect(Collectors.toList());
    }

    public void purchaseSubscription(String purchaseToken) throws IOException {
        Long shopId = SecurityUtil.getRequestedShopId();
        Shop shop = shopRepository.findById(shopId).orElseThrow(() -> new CustomException(TARGET_HINT_NOT_FOUND));

        SubscriptionPurchaseV2 purchase = androidPublisherClient.getSubscriptionPurchase(purchaseToken);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        ZonedDateTime zonedDateTime = ZonedDateTime.parse(purchase.getLineItems().get(0).getExpiryTime(), formatter);
        LocalDate expiryDate = zonedDateTime.toLocalDate();

        String planId = purchase.getLineItems().get(0).getOfferDetails().getBasePlanId();

        Subscription subscription = Subscription.builder()
            .shop(shop)
            .status(SUBSCRIPTION)
            .plan(SubscriptionPlan.getSubscriptionPlanByPlanId(planId))
            .expiryDate(expiryDate)
            .purchaseToken(purchaseToken)
            .build();

        subscriptionRepository.save(subscription);
    }

    public void renew(String purchaseToken) throws IOException {
        Subscription subscription = subscriptionRepository.findByPurchaseToken(purchaseToken)
            .orElseThrow(() -> new CustomException(TARGET_SHOP_NOT_FOUND));

        SubscriptionPurchaseV2 purchase = androidPublisherClient.getSubscriptionPurchase(purchaseToken);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        ZonedDateTime zonedDateTime = ZonedDateTime.parse(purchase.getLineItems().get(0).getExpiryTime(), formatter);
        LocalDate expiryDate = zonedDateTime.toLocalDate();

        subscription.renew(expiryDate);
    }

    public void expire(String purchaseToken) {
        Subscription subscription = subscriptionRepository.findByPurchaseToken(purchaseToken)
            .orElseThrow(() -> new CustomException(TARGET_SHOP_NOT_FOUND));
        subscription.expire();
    }
}
