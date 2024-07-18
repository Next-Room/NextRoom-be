package com.nextroom.nextRoomServer.service;

import static com.nextroom.nextRoomServer.enums.SubscriptionPlan.*;
import static com.nextroom.nextRoomServer.enums.UserStatus.*;
import static com.nextroom.nextRoomServer.exceptions.StatusCode.*;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.api.services.androidpublisher.model.SubscriptionPurchaseV2;
import com.nextroom.nextRoomServer.domain.Shop;
import com.nextroom.nextRoomServer.domain.Subscription;
import com.nextroom.nextRoomServer.dto.SubscriptionDto;
import com.nextroom.nextRoomServer.enums.EnumModel;
import com.nextroom.nextRoomServer.enums.SubscriptionPlan;
import com.nextroom.nextRoomServer.enums.UserStatus;
import com.nextroom.nextRoomServer.exceptions.CustomException;
import com.nextroom.nextRoomServer.repository.ShopRepository;
import com.nextroom.nextRoomServer.repository.SubscriptionRepository;
import com.nextroom.nextRoomServer.security.SecurityUtil;
import com.nextroom.nextRoomServer.util.Timestamped;
import com.nextroom.nextRoomServer.util.inapp.AndroidPurchaseUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;
    private final ShopRepository shopRepository;
    private final AndroidPurchaseUtils androidPurchaseUtils;
    //    private AndroidPublisherClient androidPublisherClient;

    //    {
    //        try {
    //            androidPublisherClient = new AndroidPublisherClient();
    //        } catch (Exception e) {
    //            throw new RuntimeException(e);
    //        }
    //    }

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

    @Transactional(readOnly = true)
    public SubscriptionDto.SubscriptionInfoResponse getSubscriptionInfo() {
        Long shopId = SecurityUtil.getCurrentShopId();
        Subscription subscription = subscriptionRepository.findByShopId(shopId).orElseThrow(
            () -> new CustomException(TARGET_SHOP_NOT_FOUND));

        return new SubscriptionDto.SubscriptionInfoResponse(subscription);
    }

    @Transactional
    public SubscriptionDto.UserStatusResponse getUserStatus() {
        Long shopId = SecurityUtil.getCurrentShopId();
        Subscription subscription = subscriptionRepository.findByShopId(shopId).orElseThrow(
            () -> new CustomException(TARGET_SHOP_NOT_FOUND));

        checkUserStatus(subscription);

        return new SubscriptionDto.UserStatusResponse(subscription);
    }

    private void checkUserStatus(Subscription subscription) {
        UserStatus status = subscription.getStatus();
        LocalDate expiryDate = subscription.getExpiryDate();

        if (status == FREE && expiryDate.isBefore(Timestamped.getToday())) {
            subscription.updateStatus(HOLD, expiryDate.plusYears(1), null);
            return;
        }

        if (status == HOLD && expiryDate.isBefore(Timestamped.getToday())) {
            // subscriptionRepository.delete(subscription);
        }
    }

    @Transactional(readOnly = true)
    public List<SubscriptionDto.SubscriptionPlanResponse> getSubscriptionPlan() {
        return toEnumValues(SubscriptionPlan.class);
    }

    private List<SubscriptionDto.SubscriptionPlanResponse> toEnumValues(Class<? extends EnumModel> e) {
        return Arrays
            .stream(e.getEnumConstants())
            .map(SubscriptionDto.SubscriptionPlanResponse::new)
            .collect(Collectors.toList());
    }

    public void purchaseSubscription(String purchaseToken, String subscriptionId) throws IOException {
        Long shopId = SecurityUtil.getCurrentShopId();
        Shop shop = shopRepository.findById(shopId).orElseThrow(() -> new CustomException(TARGET_HINT_NOT_FOUND));

        SubscriptionPurchaseV2 purchase = androidPurchaseUtils.verify(purchaseToken);

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

        androidPurchaseUtils.acknowledge(purchaseToken, subscriptionId);
    }

    public void renew(String purchaseToken, String subscriptionId) throws IOException {
        Subscription subscription = subscriptionRepository.findByPurchaseToken(purchaseToken)
            .orElseThrow(() -> new CustomException(TARGET_SHOP_NOT_FOUND));

        SubscriptionPurchaseV2 purchase = androidPurchaseUtils.verify(purchaseToken);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        ZonedDateTime zonedDateTime = ZonedDateTime.parse(purchase.getLineItems().get(0).getExpiryTime(), formatter);
        LocalDate expiryDate = zonedDateTime.toLocalDate();

        subscription.renew(expiryDate);

        androidPurchaseUtils.acknowledge(purchaseToken, subscriptionId);
    }

    public void expire(String purchaseToken) {
        Subscription subscription = subscriptionRepository.findByPurchaseToken(purchaseToken)
            .orElseThrow(() -> new CustomException(TARGET_SHOP_NOT_FOUND));
        subscription.expire();
    }
}
