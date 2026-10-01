package com.nextroom.nextRoomServer.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import com.nextroom.nextRoomServer.domain.Authority;
import com.nextroom.nextRoomServer.domain.Shop;
import com.nextroom.nextRoomServer.exceptions.CustomException;
import com.nextroom.nextRoomServer.repository.RedisRepository;
import com.nextroom.nextRoomServer.repository.ShopRepository;

@ExtendWith(MockitoExtension.class)
class AuthServiceUnregisterTest {

    @Mock
    private ShopRepository shopRepository;

    @Mock
    private RedisRepository redisRepository;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setAuthentication() {
        SecurityContextHolder.getContext().setAuthentication(
            new UsernamePasswordAuthenticationToken("42", null, List.of()));
    }

    @AfterEach
    void clearAuthentication() {
        SecurityContextHolder.clearContext();
    }

    private Shop activeShop() {
        return Shop.builder()
            .id(42L)
            .email("owner@nextroom.com")
            .password("encoded-password")
            .authority(Authority.ROLE_USER)
            .build();
    }

    @Test
    @DisplayName("탈퇴하면 shop을 지우지 않고 탈퇴 표시만 남긴다")
    void unregisterDoesNotDeleteRow() {
        //given
        Shop shop = activeShop();
        given(shopRepository.findById(42L)).willReturn(Optional.of(shop));

        //when
        authService.unregister();

        //then
        assertThat(shop.isWithdrawn()).isTrue();
        verify(shopRepository, never()).deleteById(any());
        verify(shopRepository, never()).delete(any());
    }

    @Test
    @DisplayName("탈퇴하면 해당 회원의 refresh token을 모두 삭제한다")
    void unregisterClearsRefreshTokens() {
        //given
        given(shopRepository.findById(42L)).willReturn(Optional.of(activeShop()));

        //when
        authService.unregister();

        //then
        verify(redisRepository).deleteValuesByPattern("RefreshToken 42 *");
    }

    @Test
    @DisplayName("이미 탈퇴한 회원이 다시 탈퇴를 요청하면 예외가 발생한다")
    void unregisterTwiceFails() {
        //given
        Shop shop = activeShop();
        shop.withdraw();
        given(shopRepository.findById(42L)).willReturn(Optional.of(shop));

        //when & then
        assertThatThrownBy(() -> authService.unregister())
            .isInstanceOf(CustomException.class);
        verify(redisRepository, never()).deleteValuesByPattern(anyString());
    }
}
