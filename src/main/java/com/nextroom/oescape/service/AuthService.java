package com.nextroom.oescape.service;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.nextroom.oescape.dto.response.TokenResponseDto;
import com.nextroom.oescape.security.TokenProvider;
import com.nextroom.oescape.domain.RefreshToken;
import com.nextroom.oescape.domain.Shop;
import com.nextroom.oescape.dto.request.ShopRequestDto;
import com.nextroom.oescape.dto.request.TokenRequestDto;
import com.nextroom.oescape.dto.response.ShopResponseDto;
import com.nextroom.oescape.repository.RefreshTokenRepository;
import com.nextroom.oescape.repository.ShopRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final ShopRepository shopRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public ShopResponseDto signup(ShopRequestDto shopRequestDto) {
        if (shopRepository.existsByAdminCode(shopRequestDto.getAdminCode())) {
            throw new RuntimeException("The shop already exists.");
        }

        Shop shop = shopRequestDto.toShop(passwordEncoder);
        return ShopResponseDto.of(shopRepository.save(shop));
    }

    @Transactional
    public TokenResponseDto login(ShopRequestDto shopRequestDto) {
        UsernamePasswordAuthenticationToken authenticationToken = shopRequestDto.toAuthentication();

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        TokenResponseDto tokenDto = tokenProvider.generateTokenDto(authentication);

        RefreshToken refreshToken = RefreshToken.builder()
            .key(authentication.getName())
            .value(tokenDto.getRefreshToken())
            .build();

        refreshTokenRepository.save(refreshToken);

        return tokenDto;
    }

    @Transactional
    public TokenResponseDto reissue(TokenRequestDto tokenRequestDto) {
        if (!tokenProvider.validateToken(tokenRequestDto.getRefreshToken())) {
            throw new RuntimeException("Refresh Token is invalid.");
        }

        Authentication authentication = tokenProvider.getAuthentication(tokenRequestDto.getAccessToken());

        RefreshToken refreshToken = refreshTokenRepository.findByKey(authentication.getName())
            .orElseThrow(() -> new RuntimeException("로그아웃 된 사용자입니다."));

        if (!refreshToken.getValue().equals(tokenRequestDto.getRefreshToken())) {
            throw new RuntimeException("토큰의 유저 정보가 일치하지 않습니다.");
        }

        TokenResponseDto tokenDto = tokenProvider.generateTokenDto(authentication);

        RefreshToken newRefreshToken = refreshToken.updateValue(tokenDto.getRefreshToken());
        refreshTokenRepository.save(newRefreshToken);

        return tokenDto;
    }
}
