package com.nextroom.nextRoomServer.service;

import static com.nextroom.nextRoomServer.exceptions.StatusCode.TARGET_PAYMENT_NOT_FOUND;
import static com.nextroom.nextRoomServer.exceptions.StatusCode.TARGET_SHOP_NOT_FOUND;

import com.google.api.services.androidpublisher.model.SubscriptionPurchaseLineItem;
import com.google.api.services.androidpublisher.model.SubscriptionPurchaseV2;
import com.nextroom.nextRoomServer.domain.Payment;
import com.nextroom.nextRoomServer.domain.Product;
import com.nextroom.nextRoomServer.domain.Shop;
import com.nextroom.nextRoomServer.domain.Subscription;
import com.nextroom.nextRoomServer.dto.PaymentDto;
import com.nextroom.nextRoomServer.dto.SubscriptionDto;
import com.nextroom.nextRoomServer.dto.SubscriptionDto.SubscriptionPlan;
import com.nextroom.nextRoomServer.enums.NotificationType;
import com.nextroom.nextRoomServer.enums.SubscriptionState;
import com.nextroom.nextRoomServer.exceptions.CustomException;
import com.nextroom.nextRoomServer.repository.PaymentRepository;
import com.nextroom.nextRoomServer.repository.ShopRepository;
import com.nextroom.nextRoomServer.repository.SubscriptionRepository;
import com.nextroom.nextRoomServer.security.SecurityUtil;
import com.nextroom.nextRoomServer.util.CommonObjectMapper;
import com.nextroom.nextRoomServer.util.Timestamped;
import com.nextroom.nextRoomServer.util.inapp.AndroidPurchaseUtils;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final ProductService productService;
    private final SubscriptionRepository subscriptionRepository;
    private final ShopRepository shopRepository;
    private final PaymentRepository paymentRepository;
    private final AndroidPurchaseUtils androidPurchaseUtils;

    @Value("${nextroom.plan-document-url}")
    private String planDocumentUrl;

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

    public SubscriptionDto.SubscriptionPlanResponse getSubscriptionPlan() {
        List<Product> productList = productService.findAll();
        List<SubscriptionPlan> plans = productList.stream()
            .map(SubscriptionPlan::new)
            .toList();
        return SubscriptionDto.SubscriptionPlanResponse.builder()
            .url(planDocumentUrl)
            .plans(plans)
            .build();
    }

    @Transactional(readOnly = true)
    public List<PaymentDto.Meta> getShopPaymentList() {
        Long shopId = SecurityUtil.getCurrentShopId();
        List<Payment> payments = paymentRepository.findAllByShopId(shopId);
        return payments.stream()
            .map(PaymentDto::toMeta)
            .toList();
    }

    @Transactional(readOnly = true)
    public PaymentDto.Detail getPaymentDetail(String orderId) {
        Payment payment = paymentRepository.findByOrderId(orderId)
            .orElseThrow(() -> new CustomException(TARGET_PAYMENT_NOT_FOUND));
        return PaymentDto.toDetail(payment);
    }

    @Transactional(rollbackFor = Exception.class)
    public void purchaseSubscription(String purchaseToken) {
        Long shopId = SecurityUtil.getCurrentShopId();
        Shop shop = getShop(shopId);

        // request Google API payment
        SubscriptionPurchaseV2 purchase = androidPurchaseUtils.verifyPurchase(purchaseToken);
        SubscriptionPurchaseLineItem lineItem = purchase.getLineItems().get(0);
        Product product = productService.getProduct(lineItem.getProductId());

        // save payment
        Payment payment = Payment.builder()
            .shop(shop)
            .product(product)
            .orderId(purchase.getLatestOrderId())
            .type(SubscriptionState.getSubscriptionType(purchase.getSubscriptionState()))
            .purchaseToken(purchaseToken)
            .receipt(CommonObjectMapper.getInstance().writeValueAsString(purchase))
            .build();
        paymentRepository.save(payment);

        // update subscription
        LocalDate startDate = Timestamped.stringToKstLocalDate(purchase.getStartTime());
        LocalDate expiryDate = Timestamped.stringToKstLocalDate(lineItem.getExpiryTime());

        Subscription subscription = getSubscription(shopId);
        subscription.subscribe(product, startDate, expiryDate);

        // confirm Google API payment
        androidPurchaseUtils.acknowledge(purchaseToken, product.getSubscriptionProductId());
    }

    @Transactional
    public void updateSubscription(SubscriptionDto.UpdateSubscription requestBody) {
        SubscriptionDto.SubscriptionNotification subscriptionNotification = androidPurchaseUtils.getSubscriptionNotification(requestBody.getData());

        String purchaseToken = subscriptionNotification.getPurchaseToken();
        NotificationType notificationType = NotificationType.of(subscriptionNotification.getNotificationType());

        switch (notificationType) {
            case SUBSCRIPTION_RENEWED -> renew(purchaseToken);
            case SUBSCRIPTION_EXPIRED -> expire(purchaseToken);
            default -> log.debug("Unsupported notification type: {}", subscriptionNotification.getNotificationType());
        }
    }

    private void renew(String purchaseToken) {
        // request Google API payment
        SubscriptionPurchaseV2 purchase = androidPurchaseUtils.verifyNotification(purchaseToken);
        SubscriptionPurchaseLineItem lineItem = purchase.getLineItems().get(0);
        Product product = productService.getProduct(lineItem.getProductId());

        // save payment
        Subscription subscription = getSubscriptionByPurchase(purchaseToken);

        Payment payment = Payment.builder()
            .shop(subscription.getShop())
            .product(product)
            .orderId(purchase.getLatestOrderId())
            .type(SubscriptionState.getSubscriptionType(purchase.getSubscriptionState()))
            .purchaseToken(purchaseToken)
            .receipt(CommonObjectMapper.getInstance().writeValueAsString(purchase))
            .build();
        paymentRepository.save(payment);

        // update subscription
        LocalDate startDate = Timestamped.stringToKstLocalDate(purchase.getStartTime());
        LocalDate expiryDate = Timestamped.stringToKstLocalDate(lineItem.getExpiryTime());

        subscription.renew(startDate, expiryDate);
    }

    private void expire(String purchaseToken) {
        // request Google API payment
        SubscriptionPurchaseV2 purchase = androidPurchaseUtils.verifyNotification(purchaseToken);
        SubscriptionPurchaseLineItem lineItem = purchase.getLineItems().get(0);
        Product product = productService.getProduct(lineItem.getProductId());

        // save payment
        Subscription subscription = getSubscriptionByPurchase(purchaseToken);

        Payment payment = Payment.builder()
            .shop(subscription.getShop())
            .product(product)
            .orderId(purchase.getLatestOrderId())
            .type(SubscriptionState.getSubscriptionType(purchase.getSubscriptionState()))
            .purchaseToken(purchaseToken)
            .receipt(CommonObjectMapper.getInstance().writeValueAsString(purchase))
            .build();
        paymentRepository.save(payment);

        // update subscription
        subscription.expire();
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

}
