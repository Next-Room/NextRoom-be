package com.nextroom.nextRoomServer.util;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nextroom.nextRoomServer.domain.Authority;
import com.nextroom.nextRoomServer.domain.Shop;
import com.nextroom.nextRoomServer.dto.SubscriptionDto.PublishedMessage;
import org.junit.jupiter.api.Test;

class CommonObjectMapperTest {

    @Test
    void readValue() throws JsonProcessingException {
        Shop shop = Shop.builder()
            .email("test")
            .adminCode("test")
            .password("test")
            .name("test")
            .type(1)
            .comment("test")
            .authority(Authority.ROLE_USER)
            .build();

        String actual = CommonObjectMapper.getInstance().writeValueAsString(shop);
        String origin = new ObjectMapper().writeValueAsString(shop);
        assertThat(actual).isEqualTo(origin);
    }

    @Test
    void readEncodedValue() {
        String data = "ewogICJ2ZXJzaW9uIjogInN0cmluZyIsCiAgInBhY2thZ2VOYW1lIjogInN0cmluZyIsCiAgImV2ZW50VGltZU1pbGxpcyI6ICI0NTY3ODk5ODciLAogICJzdWJzY3JpcHRpb25Ob3RpZmljYXRpb24iOgogIHsKICAgICJ2ZXJzaW9uIjoiMS4wIiwKICAgICJub3RpZmljYXRpb25UeXBlIjo0LAogICAgInB1cmNoYXNlVG9rZW4iOiJQVVJDSEFTRV9UT0tFTiIsCiAgICAic3Vic2NyaXB0aW9uSWQiOiJtb250aGx5MDAxIgogIH0KfQ==";
        PublishedMessage actual = CommonObjectMapper.getInstance().readEncodedValue(data, PublishedMessage.class);
        assertThat(actual.getPackageName()).isEqualTo("string");
        assertThat(actual.getSubscriptionNotification()).isNotNull();
        assertThat(actual.getSubscriptionNotification().getNotificationType()).isEqualTo(4);
        assertThat(actual.getSubscriptionNotification().getPurchaseToken()).isEqualTo("PURCHASE_TOKEN");
    }
}