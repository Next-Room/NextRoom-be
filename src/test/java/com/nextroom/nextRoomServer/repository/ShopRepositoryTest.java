package com.nextroom.nextRoomServer.repository;

import static org.assertj.core.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import com.nextroom.nextRoomServer.domain.Authority;
import com.nextroom.nextRoomServer.domain.Shop;

@DataJpaTest
class ShopRepositoryTest {

    private static final String EMAIL = "owner@nextroom.com";
    private static final String GOOGLE_SUB = "google-sub-1";

    @Autowired
    private ShopRepository shopRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Shop persistShop(String email, String googleSub, String adminCode) {
        return entityManager.persistAndFlush(Shop.builder()
            .email(email)
            .googleSub(googleSub)
            .password("encoded-password")
            .adminCode(adminCode)
            .authority(Authority.ROLE_USER)
            .build());
    }

    private void withdraw(Shop shop) {
        shop.withdraw();
        entityManager.flush();
        entityManager.clear();
    }

    @Test
    @DisplayName("탈퇴한 회원은 이메일 로그인 조회에서 제외된다")
    void emailLookupExcludesWithdrawn() {
        //given
        Shop shop = persistShop(EMAIL, null, "AAAAA");
        withdraw(shop);

        //when
        Optional<Shop> found = shopRepository.findByEmailAndGoogleSubIsNullAndDeletedAtIsNull(EMAIL);

        //then
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("탈퇴한 회원은 구글 로그인 조회에서 제외된다")
    void googleLookupExcludesWithdrawn() {
        //given
        Shop shop = persistShop(EMAIL, GOOGLE_SUB, "AAAAA");
        withdraw(shop);

        //when
        Optional<Shop> found = shopRepository.findByEmailAndGoogleSubAndDeletedAtIsNull(EMAIL, GOOGLE_SUB);

        //then
        assertThat(found).isEmpty();
    }

    // 이메일 변형 없이 deleted_at 만 찍힌 행(수동 데이터 보정 등)도 조건절만으로 걸러져야 한다.
    private void markDeletedWithoutMasking(Shop shop) {
        entityManager.getEntityManager()
            .createNativeQuery("update shop set deleted_at = current_timestamp where shop_id = :id")
            .setParameter("id", shop.getId())
            .executeUpdate();
        entityManager.clear();
    }

    @Test
    @DisplayName("이메일이 변형되지 않았어도 deleted_at이 있으면 이메일 로그인 조회에서 제외된다")
    void emailLookupExcludesDeletedRowWithOriginalEmail() {
        //given
        Shop shop = persistShop(EMAIL, null, "AAAAA");
        markDeletedWithoutMasking(shop);

        //when
        Optional<Shop> found = shopRepository.findByEmailAndGoogleSubIsNullAndDeletedAtIsNull(EMAIL);

        //then
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("이메일이 변형되지 않았어도 deleted_at이 있으면 구글 로그인 조회에서 제외된다")
    void googleLookupExcludesDeletedRowWithOriginalEmail() {
        //given
        Shop shop = persistShop(EMAIL, GOOGLE_SUB, "AAAAA");
        markDeletedWithoutMasking(shop);

        //when
        Optional<Shop> found = shopRepository.findByEmailAndGoogleSubAndDeletedAtIsNull(EMAIL, GOOGLE_SUB);

        //then
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("탈퇴하지 않은 회원은 정상 조회된다")
    void activeShopIsFound() {
        //given
        persistShop(EMAIL, null, "AAAAA");

        //when
        Optional<Shop> found = shopRepository.findByEmailAndGoogleSubIsNullAndDeletedAtIsNull(EMAIL);

        //then
        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo(EMAIL);
    }

    @Test
    @DisplayName("탈퇴 후 같은 이메일로 재가입할 수 있다")
    void canRejoinWithSameEmail() {
        //given
        Shop withdrawn = persistShop(EMAIL, null, "AAAAA");
        withdraw(withdrawn);

        //when
        Shop rejoined = persistShop(EMAIL, null, "BBBBB");
        entityManager.clear();

        //then
        Optional<Shop> found = shopRepository.findByEmailAndGoogleSubIsNullAndDeletedAtIsNull(EMAIL);
        assertThat(found).isPresent();
        assertThat(found.get().getId()).isEqualTo(rejoined.getId());
    }

    @Test
    @DisplayName("adminCode 중복 검사는 탈퇴한 회원의 코드도 포함한다")
    void adminCodeCheckIncludesWithdrawn() {
        //given
        Shop shop = persistShop(EMAIL, null, "AAAAA");
        withdraw(shop);

        //when
        boolean exists = shopRepository.existsByAdminCode("AAAAA");

        //then
        assertThat(exists).isTrue();
    }
}
