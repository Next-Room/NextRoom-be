package com.nextroom.nextRoomServer.service;

import static com.nextroom.nextRoomServer.enums.SubscriptionPlan.MINI;
import static com.nextroom.nextRoomServer.enums.UserStatus.SUBSCRIPTION;
import static com.nextroom.nextRoomServer.exceptions.StatusCode.INTERNAL_SERVER_ERROR;
import static com.nextroom.nextRoomServer.exceptions.StatusCode.TARGET_PAYMENT_NOT_FOUND;
import static com.nextroom.nextRoomServer.exceptions.StatusCode.TARGET_SHOP_NOT_FOUND;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.services.androidpublisher.model.SubscriptionPurchaseV2;
import com.nextroom.nextRoomServer.domain.Payment;
import com.nextroom.nextRoomServer.domain.Shop;
import com.nextroom.nextRoomServer.domain.Subscription;
import com.nextroom.nextRoomServer.dto.PaymentDto;
import com.nextroom.nextRoomServer.dto.SubscriptionDto;
import com.nextroom.nextRoomServer.enums.SubscriptionPlan;
import com.nextroom.nextRoomServer.exceptions.CustomException;
import com.nextroom.nextRoomServer.repository.PaymentRepository;
import com.nextroom.nextRoomServer.repository.ShopRepository;
import com.nextroom.nextRoomServer.repository.SubscriptionRepository;
import com.nextroom.nextRoomServer.security.SecurityUtil;
import com.nextroom.nextRoomServer.util.inapp.AndroidPurchaseUtils;
import java.io.IOException;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    private final SubscriptionRepository subscriptionRepository;
    private final ShopRepository shopRepository;
    private final PaymentRepository paymentRepository;
    private final AndroidPurchaseUtils androidPurchaseUtils;
    private final ObjectMapper objectMapper;

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
            SubscriptionPurchaseV2 purchase = androidPurchaseUtils.verify(purchaseToken);

            ZonedDateTime zonedDateTime = ZonedDateTime.parse(purchase.getLineItems().get(0).getExpiryTime(), FORMATTER);
            LocalDate expiryDate = zonedDateTime.toLocalDate();

            String planId = purchase.getLineItems().get(0).getOfferDetails().getBasePlanId();

            // save subscription
            Subscription subscription = Subscription.builder()
                .shop(shop)
                .status(SUBSCRIPTION)
                .plan(SubscriptionPlan.getSubscriptionPlanByPlanId(planId))
                .expiryDate(expiryDate)
                .build();
            subscriptionRepository.save(subscription);

            // save payment
            Payment payment = Payment.builder()
                .shop(shop)
                .purchaseToken(purchaseToken)
                .subscriptionId(subscription.getId())
                .type(subscription.getPlan())
                .receipt(objectMapper.writeValueAsString(purchase))
                .build();
            paymentRepository.save(payment);

            // confirm Google API payment
            androidPurchaseUtils.acknowledge(purchaseToken, subscriptionId);
        } catch (IOException e) {
            throw new CustomException(INTERNAL_SERVER_ERROR);  // FIXME: Throwable e.getMessage()
        }
    }

    public void renew(String purchaseToken, String subscriptionId) throws IOException {
        Subscription subscription = subscriptionRepository.findByPurchaseToken(purchaseToken)
            .orElseThrow(() -> new CustomException(TARGET_SHOP_NOT_FOUND));

        SubscriptionPurchaseV2 purchase = androidPurchaseUtils.verify(purchaseToken);

        ZonedDateTime zonedDateTime = ZonedDateTime.parse(purchase.getLineItems().get(0).getExpiryTime(), FORMATTER);
        LocalDate expiryDate = zonedDateTime.toLocalDate();

        subscription.renew(expiryDate);

        androidPurchaseUtils.acknowledge(purchaseToken, subscriptionId);
    }

    public void expire(String purchaseToken) {
        Subscription subscription = subscriptionRepository.findByPurchaseToken(purchaseToken)
            .orElseThrow(() -> new CustomException(TARGET_SHOP_NOT_FOUND));
        subscription.expire();
    }

    public List<PaymentDto.Meta> getShopPaymentList() {
        Long shopId = SecurityUtil.getCurrentShopId();
        List<Payment> payments = paymentRepository.findAllByShopId(shopId);
        return payments.stream()
            .map(PaymentDto::toMeta)
            .collect(Collectors.toList());
    }

    public PaymentDto.Detail getPaymentDetail(String transactionId) {
        Payment payment = paymentRepository.findByTransactionId(transactionId)
            .orElseThrow(() -> new CustomException(TARGET_PAYMENT_NOT_FOUND));
        return PaymentDto.toDetail(payment);
    }

    private Shop getShop(Long shopId) {
        return shopRepository.findById(shopId)
            .orElseThrow(() -> new CustomException(TARGET_SHOP_NOT_FOUND));
    }
}
