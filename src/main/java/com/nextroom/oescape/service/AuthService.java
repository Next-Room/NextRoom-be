package com.nextroom.oescape.service;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.nextroom.oescape.config.security.TokenDto;
import com.nextroom.oescape.config.security.TokenProvider;
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
    public TokenDto login(ShopRequestDto shopRequestDto) {
        // 1. Admin Code 를 기반으로 AuthenticationToken 생성
        UsernamePasswordAuthenticationToken authenticationToken = shopRequestDto.toAuthentication();

        // 2. 실제로 검증 (사용자 비밀번호 체크) 이 이루어지는 부분
        //    authenticate 메서드가 실행이 될 때 CustomUserDetailsService 에서 만들었던 loadUserByUsername 메서드가 실행됨
        try {
            Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
            System.out.println("hit3");

            // 3. 인증 정보를 기반으로 JWT 토큰 생성
            TokenDto tokenDto = tokenProvider.generateTokenDto(authentication);

            // 4. RefreshToken 저장
            RefreshToken refreshToken = RefreshToken.builder()
                .key(authentication.getName())
                .value(tokenDto.getRefreshToken())
                .build();

            refreshTokenRepository.save(refreshToken);

            // 5. 토큰 발급
            return tokenDto;
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        return null;
    }

    @Transactional
    public TokenDto reissue(TokenRequestDto tokenRequestDto) {
        // 1. Refresh Token 검증
        if (!tokenProvider.validateToken(tokenRequestDto.getRefreshToken())) {
            throw new RuntimeException("Refresh Token is invalid.");
        }

        // 2. Access Token 에서 Shop ID 가져오기
        Authentication authentication = tokenProvider.getAuthentication(tokenRequestDto.getAccessToken());

        // 3. 저장소에서 Member ID 를 기반으로 Refresh Token 값 가져옴
        RefreshToken refreshToken = refreshTokenRepository.findByKey(authentication.getName())
            .orElseThrow(() -> new RuntimeException("로그아웃 된 사용자입니다."));

        // 4. Refresh Token 일치하는지 검사
        if (!refreshToken.getValue().equals(tokenRequestDto.getRefreshToken())) {
            throw new RuntimeException("토큰의 유저 정보가 일치하지 않습니다.");
        }

        // 5. 새로운 토큰 생성
        TokenDto tokenDto = tokenProvider.generateTokenDto(authentication);

        // 6. 저장소 정보 업데이트
        RefreshToken newRefreshToken = refreshToken.updateValue(tokenDto.getRefreshToken());
        refreshTokenRepository.save(newRefreshToken);

        // 토큰 발급
        return tokenDto;
    }
}
