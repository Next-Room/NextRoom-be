package com.nextroom.nextRoomServer.service;

import static com.nextroom.nextRoomServer.enums.UserStatus.*;
import static com.nextroom.nextRoomServer.exceptions.StatusCode.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.services.androidpublisher.model.SubscriptionPurchaseLineItem;
import com.google.api.services.androidpublisher.model.SubscriptionPurchaseV2;
import com.nextroom.nextRoomServer.domain.Payment;
import com.nextroom.nextRoomServer.domain.Shop;
import com.nextroom.nextRoomServer.domain.Subscription;
import com.nextroom.nextRoomServer.dto.PaymentDto;
import com.nextroom.nextRoomServer.dto.SubscriptionDto;
import com.nextroom.nextRoomServer.enums.NotificationType;
import com.nextroom.nextRoomServer.enums.SubscriptionPlan;
import com.nextroom.nextRoomServer.enums.SubscriptionState;
import com.nextroom.nextRoomServer.exceptions.CustomException;
import com.nextroom.nextRoomServer.repository.PaymentRepository;
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
    private final PaymentRepository paymentRepository;
    private final AndroidPurchaseUtils androidPurchaseUtils;
    private final ObjectMapper objectMapper;

    @Transactional(readOnly = true)
    public SubscriptionDto.SubscriptionInfoResponse getSubscriptionInfo() {
        Long shopId = SecurityUtil.getCurrentShopId();
        Subscription subscription = getSubscription(shopId);

        return new SubscriptionDto.SubscriptionInfoResponse(subscription);
    }

    @Transactional
    public SubscriptionDto.UserStatusResponse getUserStatus() {
        Long shopId = SecurityUtil.getCurrentShopId();
        Subscription subscription = getSubscription(shopId);

        subscription.checkStatus();

        return new SubscriptionDto.UserStatusResponse(subscription);
    }

    public List<SubscriptionDto.SubscriptionPlanResponse> getSubscriptionPlan() {
        return SubscriptionPlan.toList();
    }

    @Transactional(rollbackFor = Exception.class)
    public void purchaseSubscription(String purchaseToken, String subscriptionId) {
        Long shopId = SecurityUtil.getCurrentShopId();
        Shop shop = getShop(shopId);

        try {
            // request Google API payment
            SubscriptionPurchaseV2 purchase = androidPurchaseUtils.verifyPurchase(purchaseToken);
            SubscriptionPurchaseLineItem lineItem = purchase.getLineItems().get(0);

            // save payment
            Payment payment = Payment.builder()
                .shop(shop)
                .orderId(purchase.getLatestOrderId())
                .productId(lineItem.getProductId())
                .type(SubscriptionState.getSubscriptionType(purchase.getSubscriptionState()))
                .purchaseToken(purchaseToken)
                .receipt(objectMapper.writeValueAsString(purchase))
                .build();
            paymentRepository.save(payment);

            // save subscription
            Subscription subscription = subscriptionRepository.findByShopId(shopId)
                .orElse(Subscription.builder()
                    .shop(shop)
                    .status(SUBSCRIPTION)
                    .plan(SubscriptionPlan.getSubscriptionPlanByPlanId(lineItem.getProductId()))
                    .startDate(Timestamped.stringToKstLocalDate(purchase.getStartTime()))
                    .expiryDate(Timestamped.stringToKstLocalDate(lineItem.getExpiryTime()))
                    .build());
            subscriptionRepository.save(subscription);

            // confirm Google API payment
            androidPurchaseUtils.acknowledge(purchaseToken, subscriptionId);
        } catch (IOException e) {
            throw new CustomException(INTERNAL_SERVER_ERROR);  // FIXME: Throwable e.getMessage()
        }
    }

    @Transactional
    public void updateSubscription(SubscriptionDto.UpdateSubscription requestBody) {
        try {
            SubscriptionDto.SubscriptionNotification subscriptionNotification = androidPurchaseUtils.getSubscriptionNotification(
                requestBody.getMessage().getData());

            int notificationType = subscriptionNotification.getNotificationType();
            String purchaseToken = subscriptionNotification.getPurchaseToken();

            if (isRenew(notificationType)) {
                renew(purchaseToken);
            }

            if (isExpired(notificationType)) {
                expire(purchaseToken);
            }
        } catch (IOException e) {
            throw new CustomException(INTERNAL_SERVER_ERROR);  // FIXME: Throwable e.getMessage()
        }

    }

    @Transactional
    public void renew(String purchaseToken) throws IOException {
        try {
            // request Google API payment
            SubscriptionPurchaseV2 purchase = androidPurchaseUtils.verifyNotification(purchaseToken);
            SubscriptionPurchaseLineItem lineItem = purchase.getLineItems().get(0);

            // save payment
            Subscription subscription = getSubscriptionByPurchase(purchaseToken);

            Payment payment = Payment.builder()
                .shop(subscription.getShop())
                .orderId(purchase.getLatestOrderId())
                .productId(lineItem.getProductId())
                .type(SubscriptionState.getSubscriptionType(purchase.getSubscriptionState()))
                .purchaseToken(purchaseToken)
                .receipt(objectMapper.writeValueAsString(purchase))
                .build();
            paymentRepository.save(payment);

            // update subscription
            LocalDate startDate = Timestamped.stringToKstLocalDate(purchase.getStartTime());
            LocalDate expiryDate = Timestamped.stringToKstLocalDate(lineItem.getExpiryTime());

            subscription.renew(startDate, expiryDate);
        } catch (IOException e) {
            throw new CustomException(INTERNAL_SERVER_ERROR);  // FIXME: Throwable e.getMessage()
        }
    }

    @Transactional
    public void expire(String purchaseToken) {
        try {
            // request Google API payment
            SubscriptionPurchaseV2 purchase = androidPurchaseUtils.verifyNotification(purchaseToken);
            SubscriptionPurchaseLineItem lineItem = purchase.getLineItems().get(0);

            // save payment
            Subscription subscription = getSubscriptionByPurchase(purchaseToken);

            Payment payment = Payment.builder()
                .shop(subscription.getShop())
                .orderId(purchase.getLatestOrderId())
                .productId(lineItem.getProductId())
                .type(SubscriptionState.getSubscriptionType(purchase.getSubscriptionState()))
                .purchaseToken(purchaseToken)
                .receipt(objectMapper.writeValueAsString(purchase))
                .build();
            paymentRepository.save(payment);

            // delete subscription
            subscriptionRepository.deleteById(subscription.getId());
        } catch (IOException e) {
            throw new CustomException(INTERNAL_SERVER_ERROR);  // FIXME: Throwable e.getMessage()
        }
    }

    public List<PaymentDto.Meta> getShopPaymentList() {
        Long shopId = SecurityUtil.getCurrentShopId();
        List<Payment> payments = paymentRepository.findAllByShopId(shopId);
        return payments.stream()
            .map(PaymentDto::toMeta)
            .toList();
    }

    public PaymentDto.Detail getPaymentDetail(String orderId) {
        Payment payment = paymentRepository.findByOrderId(orderId)
            .orElseThrow(() -> new CustomException(TARGET_PAYMENT_NOT_FOUND));
        return PaymentDto.toDetail(payment);
    }

    private Shop getShop(Long shopId) {
        return shopRepository.findById(shopId)
            .orElseThrow(() -> new CustomException(TARGET_SHOP_NOT_FOUND));
    }

    private Subscription getSubscription(Long shopId) {
        return subscriptionRepository.findByShopId(shopId)
            .orElseThrow(() -> new CustomException(TARGET_SHOP_NOT_FOUND));
    }

    private Subscription getSubscriptionByPurchase(String purchaseToken) {
        Payment payment = paymentRepository.findFirstByPurchaseTokenOrderByCreatedAt(purchaseToken)
            .orElseThrow(() -> new CustomException(TARGET_PAYMENT_NOT_FOUND));
        return getSubscription(payment.getShop().getId());
    }

    private boolean isRenew(int notificationType) {
        return notificationType == NotificationType.SUBSCRIPTION_RENEWED.getType();
    }

    private boolean isExpired(int notificationType) {
        return notificationType == NotificationType.SUBSCRIPTION_EXPIRED.getType();
    }
}
