package com.nextroom.nextRoomServer.domain;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.nextroom.nextRoomServer.exceptions.CustomException;
import com.nextroom.nextRoomServer.exceptions.StatusCode;

class ShopTest {

    private Shop shop(String email, String googleSub) {
        return Shop.builder()
            .id(42L)
            .email(email)
            .googleSub(googleSub)
            .password("encoded-password")
            .name("넥스트룸 강남점")
            .authority(Authority.ROLE_USER)
            .build();
    }

    @Test
    @DisplayName("탈퇴하면 deletedAt이 찍히고 이메일이 변형되며 비밀번호가 제거된다")
    void withdraw() {
        //given
        Shop shop = shop("owner@nextroom.com", null);

        //when
        shop.withdraw();

        //then
        assertThat(shop.isWithdrawn()).isTrue();
        assertThat(shop.getDeletedAt()).isNotNull();
        assertThat(shop.getEmail()).isEqualTo("withdrawn_42_owner@nextroom.com");
        assertThat(shop.getPassword()).isNull();
    }

    @Test
    @DisplayName("탈퇴해도 매장명과 googleSub는 거래 주체 식별 정보로 보존한다")
    void withdrawKeepsIdentityFields() {
        //given
        Shop shop = shop("owner@nextroom.com", "google-sub-1");

        //when
        shop.withdraw();

        //then
        assertThat(shop.getGoogleSub()).isEqualTo("google-sub-1");
        assertThat(shop.getName()).isEqualTo("넥스트룸 강남점");
    }

    @Test
    @DisplayName("이미 탈퇴한 회원이 다시 탈퇴를 요청하면 예외가 발생한다")
    void withdrawTwice() {
        //given
        Shop shop = shop("owner@nextroom.com", null);
        shop.withdraw();

        //when & then
        assertThatThrownBy(shop::withdraw)
            .isInstanceOf(CustomException.class)
            .extracting("statusCode")
            .isEqualTo(StatusCode.SHOP_ALREADY_WITHDRAWN);

        // 이메일이 이중으로 변형되지 않아야 한다
        assertThat(shop.getEmail()).isEqualTo("withdrawn_42_owner@nextroom.com");
    }

    @Test
    @DisplayName("이메일이 없는 계정도 탈퇴할 수 있다")
    void withdrawWithoutEmail() {
        //given
        Shop shop = shop(null, "google-sub-1");

        //when
        shop.withdraw();

        //then
        assertThat(shop.isWithdrawn()).isTrue();
        assertThat(shop.getEmail()).isNull();
    }

    @Test
    @DisplayName("이메일이 아주 길어도 컬럼 길이를 넘지 않게 자른다")
    void withdrawTruncatesLongEmail() {
        //given
        String longEmail = "a".repeat(250) + "@nextroom.com";
        Shop shop = shop(longEmail, null);

        //when
        shop.withdraw();

        //then
        assertThat(shop.getEmail()).hasSize(255);
        assertThat(shop.getEmail()).startsWith("withdrawn_42_");
    }
}
