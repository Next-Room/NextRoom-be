package com.nextroom.nextRoomServer.dto;

import com.nextroom.nextRoomServer.domain.Product;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

public class ProductDto {

    @Getter
    @Builder
    @RequiredArgsConstructor
    @NoArgsConstructor(force = true)
    public static class AddProductRequest {
        private final String subscriptionProductId;
        private final String planId;
        private final String productName;
        private final String description;
        private final String subDescription;
        private final Integer originPrice;
        private final Integer sellPrice;
        private final Integer discountRate;

        public Product toProduct() {
            return Product.builder()
                .subscriptionProductId(this.subscriptionProductId)
                .planId(this.planId)
                .productName(this.productName)
                .description(this.description)
                .subDescription(this.subDescription)
                .originPrice(this.originPrice)
                .sellPrice(this.sellPrice)
                .discountRate(this.discountRate)
                .build();
        }
    }
}
