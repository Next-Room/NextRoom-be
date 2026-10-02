package com.nextroom.nextRoomServer.dto;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nextroom.nextRoomServer.domain.Authority;
import com.nextroom.nextRoomServer.domain.Shop;

class AuthDtoResponseTest {

    // Spring Boot 기본 ObjectMapper 와 같이 null 필드도 키로 내보내는 설정이다.
    // 필드 값이 null 이어도 키가 남으면 실패하도록, 값이 아니라 키의 존재를 검사한다.
    private final ObjectMapper objectMapper = new ObjectMapper();

    private Shop shop() {
        Shop shop = Shop.builder()
            .id(1L)
            .email("owner@nextroom.com")
            .name("넥스트룸 강남점")
            .authority(Authority.ROLE_USER)
            .build();
        ReflectionTestUtils.setField(shop, "createdAt", LocalDateTime.of(2026, 10, 2, 10, 0));
        ReflectionTestUtils.setField(shop, "modifiedAt", LocalDateTime.of(2026, 10, 2, 10, 0));
        return shop;
    }

    private TokenDto token() {
        return TokenDto.builder()
            .grantType("Bearer")
            .accessToken("access")
            .accessTokenExpiresIn(0L)
            .refreshToken("refresh")
            .build();
    }

    @Test
    @DisplayName("회원가입 응답에 adminCode 키가 없다")
    void signUpResponseHasNoAdminCode() {
        JsonNode json = objectMapper.valueToTree(AuthDto.SignUpResponseDto.toSignUpResponseDto(shop()));

        assertThat(json.has("adminCode")).isFalse();
        assertThat(json.get("email").asText()).isEqualTo("owner@nextroom.com");
    }

    @Test
    @DisplayName("로그인 응답에 adminCode 키가 없다")
    void logInResponseHasNoAdminCode() {
        JsonNode json = objectMapper.valueToTree(AuthDto.LogInResponseDto.toLogInResponseDto(shop(), token()));

        assertThat(json.has("adminCode")).isFalse();
        assertThat(json.get("accessToken").asText()).isEqualTo("access");
    }

    @Test
    @DisplayName("매장정보 수정 응답에 adminCode 키가 없다")
    void shopUpdateResponseHasNoAdminCode() {
        JsonNode json = objectMapper.valueToTree(AuthDto.ShopUpdateResponseDto.toShopUpdateResponseDto(shop()));

        assertThat(json.has("adminCode")).isFalse();
        assertThat(json.get("shopName").asText()).isEqualTo("넥스트룸 강남점");
    }
}
