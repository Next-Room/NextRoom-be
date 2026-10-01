package com.nextroom.nextRoomServer.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import com.nextroom.nextRoomServer.domain.Authority;
import com.nextroom.nextRoomServer.domain.Shop;
import com.nextroom.nextRoomServer.dto.AuthDto;
import com.nextroom.nextRoomServer.dto.TokenDto;
import com.nextroom.nextRoomServer.exceptions.CustomException;
import com.nextroom.nextRoomServer.exceptions.StatusCode;
import com.nextroom.nextRoomServer.repository.RedisRepository;
import com.nextroom.nextRoomServer.repository.ShopRepository;
import com.nextroom.nextRoomServer.security.TokenProvider;

@ExtendWith(MockitoExtension.class)
class AuthServiceReissueTest {

    private static final String ACCESS_TOKEN = "access-token";
    private static final String REFRESH_TOKEN = "refresh-token";

    @Mock
    private ShopRepository shopRepository;

    @Mock
    private RedisRepository redisRepository;

    @Mock
    private TokenProvider tokenProvider;

    @InjectMocks
    private AuthService authService;

    private final AuthDto.ReissueRequestDto request = new AuthDto.ReissueRequestDto(ACCESS_TOKEN, REFRESH_TOKEN);

    @BeforeEach
    void validRefreshTokenInRedis() {
        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        given(tokenProvider.validateToken(REFRESH_TOKEN)).willReturn(true);
        given(tokenProvider.getAuthentication(ACCESS_TOKEN)).willReturn(
            new UsernamePasswordAuthenticationToken(new User("42", "", authorities), null, authorities));
        given(redisRepository.getValues("RefreshToken 42 " + REFRESH_TOKEN)).willReturn(REFRESH_TOKEN);
    }

    private Shop shop() {
        return Shop.builder()
            .id(42L)
            .email("owner@nextroom.com")
            .authority(Authority.ROLE_USER)
            .build();
    }

    @Test
    @DisplayName("탈퇴한 회원은 Redis에 refresh token이 남아 있어도 재발급받을 수 없다")
    void withdrawnShopCannotReissue() {
        //given
        Shop shop = shop();
        shop.withdraw();
        given(shopRepository.findById(42L)).willReturn(Optional.of(shop));

        //when & then
        assertThatThrownBy(() -> authService.reissue(request))
            .isInstanceOf(CustomException.class)
            .extracting("statusCode")
            .isEqualTo(StatusCode.INVALID_REFRESH_TOKEN);
        verify(tokenProvider, never()).generateTokenDto(anyString(), anyString());
    }

    @Test
    @DisplayName("탈퇴하지 않은 회원은 정상적으로 재발급받는다")
    void activeShopReissues() {
        //given
        given(shopRepository.findById(42L)).willReturn(Optional.of(shop()));
        given(tokenProvider.generateTokenDto("42", "ROLE_USER")).willReturn(TokenDto.builder()
            .grantType("Bearer")
            .accessToken("new-access")
            .refreshToken("new-refresh")
            .build());

        //when
        AuthDto.ReissueResponseDto response = authService.reissue(request);

        //then
        assertThat(response.getAccessToken()).isEqualTo("new-access");
    }
}
