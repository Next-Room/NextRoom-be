package com.nextroom.nextRoomServer.dto;

import com.nextroom.nextRoomServer.domain.Product;

import lombok.Getter;

public class ProductDto {

    @Getter
    public static class AddProductRequest {
        private String subscriptionProductId;
        private String planId;
        private String productName;
        private String description;
        private String subDescription;
        private Integer originPrice;
        private Integer sellPrice;
        private Integer discountRate;

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
