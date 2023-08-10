package com.nextroom.nextRoomServer.repository;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.nextroom.nextRoomServer.domain.Shop;
import com.nextroom.nextRoomServer.domain.Theme;
import com.nextroom.nextRoomServer.exceptions.CustomException;
import com.nextroom.nextRoomServer.exceptions.StatusCode;

@DataJpaTest
public class ThemeRepositoryTest {

    @Autowired
    private ThemeRepository themeRepository;

    @Test
    @DisplayName("테마 생성")
    void save() {
        //given
        Shop shop = new Shop();

        Theme theme = Theme.builder()
            .shop(shop)
            .title("테마 이름")
            .timeLimit(70)
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
        Shop shop = new Shop();

        Theme theme = Theme.builder()
            .shop(shop)
            .title("테마 이름")
            .timeLimit(70)
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
