package com.nextroom.nextRoomServer.service;

import static com.nextroom.nextRoomServer.exceptions.StatusCode.TARGET_PAYMENT_NOT_FOUND;

import com.google.api.services.androidpublisher.model.SubscriptionPurchaseV2;
import com.nextroom.nextRoomServer.domain.Payment;
import com.nextroom.nextRoomServer.domain.Product;
import com.nextroom.nextRoomServer.domain.Shop;
import com.nextroom.nextRoomServer.dto.PaymentDto;
import com.nextroom.nextRoomServer.enums.SubscriptionState;
import com.nextroom.nextRoomServer.exceptions.CustomException;
import com.nextroom.nextRoomServer.repository.PaymentRepository;
import com.nextroom.nextRoomServer.security.SecurityUtil;
import com.nextroom.nextRoomServer.util.CommonObjectMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;

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

    @Transactional
    public Payment renewPayment(Product product, SubscriptionPurchaseV2 purchase, String token) {
        Payment prePayment = paymentRepository.findFirstByPurchaseTokenOrderByCreatedAt(token)
            .orElseThrow(() -> new CustomException(TARGET_PAYMENT_NOT_FOUND));
        return savePayment(prePayment.getShop(), product, purchase, token);
    }

    @Transactional
    public Payment savePayment(Shop shop, Product product, SubscriptionPurchaseV2 purchase, String token) {
        // save payment
        Payment payment = Payment.builder()
            .shop(shop)
            .product(product)
            .orderId(purchase.getLatestOrderId())
            .type(SubscriptionState.getSubscriptionType(purchase.getSubscriptionState()))
            .purchaseToken(token)
            .receipt(CommonObjectMapper.getInstance().writeValueAsString(purchase))
            .build();
        return paymentRepository.save(payment);
    }

}
