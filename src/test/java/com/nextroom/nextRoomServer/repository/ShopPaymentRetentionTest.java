package com.nextroom.nextRoomServer.repository;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import com.nextroom.nextRoomServer.domain.Authority;
import com.nextroom.nextRoomServer.domain.Payment;
import com.nextroom.nextRoomServer.domain.Product;
import com.nextroom.nextRoomServer.domain.Shop;

import jakarta.persistence.PersistenceException;

@DataJpaTest
class ShopPaymentRetentionTest {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Shop persistShopWithPayment() {
        Shop shop = entityManager.persistAndFlush(Shop.builder()
            .email("owner@nextroom.com")
            .authority(Authority.ROLE_USER)
            .build());

        Product product = entityManager.persistAndFlush(Product.builder()
            .subscriptionProductId("nextroom.subscription")
            .planId("monthly")
            .productName("월 구독")
            .description("설명")
            .subDescription("부가 설명")
            .originPrice(10000)
            .sellPrice(9000)
            .discountRate(10)
            .build());

        entityManager.persistAndFlush(Payment.builder()
            .shop(shop)
            .product(product)
            .orderId("GPA.1234-5678-9012-34567")
            .type(1)
            .purchaseToken("purchase-token")
            .receipt("{}")
            .build());

        return shop;
    }

    @Test
    @DisplayName("shop이 탈퇴 처리되어도 결제 기록은 보존된다")
    void paymentSurvivesWithdrawal() {
        //given
        Shop shop = persistShopWithPayment();

        //when
        shop.withdraw();
        entityManager.flush();
        entityManager.clear();

        //then
        List<Payment> payments = paymentRepository.findAllByShopId(shop.getId());
        assertThat(payments).hasSize(1);
        assertThat(payments.get(0).getOrderId()).isEqualTo("GPA.1234-5678-9012-34567");
    }

    @Test
    @DisplayName("shop을 하드 삭제해도 결제 기록으로 삭제가 전파되지 않는다")
    void paymentIsNotCascadeDeleted() {
        //given
        Long shopId = persistShopWithPayment().getId();
        entityManager.clear();
        Shop shop = entityManager.find(Shop.class, shopId);

        //when & then
        // cascade가 없으면 payment가 shop을 참조하고 있어 FK 제약으로 삭제가 거부된다.
        entityManager.remove(shop);
        assertThatThrownBy(() -> entityManager.flush())
            .isInstanceOf(PersistenceException.class);
    }
}
