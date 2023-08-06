package com.nextroom.oescape.repository;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.nextroom.oescape.domain.Authority;
import com.nextroom.oescape.domain.Shop;
import com.nextroom.oescape.domain.Theme;
import com.nextroom.oescape.exceptions.CustomException;
import com.nextroom.oescape.exceptions.StatusCode;

@DataJpaTest
public class ThemeRepositoryTest {

    @Autowired
    private ThemeRepository themeRepository;
    @Autowired
    private ShopRepository shopRepository;

    @Test
    @DisplayName("Repository가 null이 아님")
    void notNull() {
        assertThat(themeRepository).isNotNull();
    }

    @Test
    @DisplayName("테마 생성")
    void save() {
        //given
        Shop shop = Shop.builder()
            .adminCode("11111")
            .password("super1234!")
            .name("test")
            .authority(Authority.ROLE_USER)
            .build();

        Shop savedShop = shopRepository.save(shop);

        Theme theme = Theme.builder()
            .shop(savedShop)
            .title("테마 이름")
            .timeLimit(70)
            .hintLimit(10)
            .build();

        //when
        Theme result = themeRepository.save(theme);

        //then
        assertThat(result.getId()).isNotNull();
        assertThat(result.getShop()).isEqualTo(shop);
        assertThat(result.getTitle()).isEqualTo("테마 이름");
        assertThat(result.getTimeLimit()).isEqualTo(70);
    }

    @Test
    @DisplayName("테마 제목으로 조회")
    void findByTitle() {
        //given
        Shop shop = Shop.builder()
            .adminCode("11111")
            .password("super1234!")
            .name("test")
            .authority(Authority.ROLE_USER)
            .build();

        Shop savedShop = shopRepository.save(shop);

        Theme theme = Theme.builder()
            .shop(savedShop)
            .title("테마 이름")
            .timeLimit(70)
            .hintLimit(10)
            .build();

        //when
        themeRepository.save(theme);
        Theme result = themeRepository.findByTitle("테마 이름").orElseThrow(
            () -> new CustomException(StatusCode.BAD_REQUEST)
        );

        //then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isNotNull();
        assertThat(result.getShop()).isEqualTo(shop);
        assertThat(result.getTitle()).isEqualTo("테마 이름");
        assertThat(result.getTimeLimit()).isEqualTo(70);
    }
}
