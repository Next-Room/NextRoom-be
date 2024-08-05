package com.nextroom.nextRoomServer.domain;

import com.nextroom.nextRoomServer.util.Timestamped;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id", nullable = false)
    private Long id;

    @Column(name = "subscription_product_id", nullable = false)
    private String subscriptionProductId;

    @Column(name = "plan_id", nullable = false)
    private String planId;

    @Column(name = "product_name", nullable = false)
    private String productName;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "sub_description", nullable = false)
    private String subDescription;

    @Column(name = "origin_price", nullable = false)
    private String originPrice;

    @Column(name = "sell_price", nullable = false)
    private String sellPrice;

    @Column(name = "discount_rate", nullable = false)
    private String discountRate;
}
