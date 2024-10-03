package com.nextroom.nextRoomServer.service;

import static com.nextroom.nextRoomServer.enums.UserStatus.*;
import static com.nextroom.nextRoomServer.exceptions.StatusCode.*;
import static com.nextroom.nextRoomServer.util.Timestamped.*;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import com.nextroom.nextRoomServer.domain.Shop;
import com.nextroom.nextRoomServer.domain.Subscription;
import com.nextroom.nextRoomServer.dto.AuthDto;
import com.nextroom.nextRoomServer.dto.TokenDto;
import com.nextroom.nextRoomServer.exceptions.CustomException;
import com.nextroom.nextRoomServer.repository.RedisRepository;
import com.nextroom.nextRoomServer.repository.ShopRepository;
import com.nextroom.nextRoomServer.repository.SubscriptionRepository;
import com.nextroom.nextRoomServer.security.SecurityUtil;
import com.nextroom.nextRoomServer.security.TokenProvider;
import com.nextroom.nextRoomServer.util.RandomCodeGenerator;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final ShopRepository shopRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final RedisRepository redisRepository;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final RandomCodeGenerator randomCodeGenerator;

    @Value("${jwt.refresh-token-expiration-millis}")
    private long refreshTokenExpirationMillis;
    private static final String REFRESH_TOKEN_PREFIX = "RefreshToken ";

    @Transactional
    public AuthDto.SignUpResponse signUp(AuthDto.SignUpRequest request) {
        if (shopRepository.existsByEmail(request.getEmail())) {
            throw new CustomException(SHOP_ALREADY_EXIST);
        }

        String adminCode = createAdminCode();
        Shop shop = shopRepository.save(request.toShop(passwordEncoder, adminCode));
        createSubscription(shop);

        return AuthDto.SignUpResponse.builder()
            .email(shop.getEmail())
            .name(shop.getName())
            .adminCode(shop.getAdminCode())
            .createdAt(dateTimeFormatter(shop.getCreatedAt()))
            .modifiedAt(dateTimeFormatter(shop.getModifiedAt())).build();
    }

    private String createAdminCode() {
        String adminCode;
        do {
            adminCode = randomCodeGenerator.createCode(5);
        } while (shopRepository.existsByAdminCode(adminCode));
        return adminCode;
    }

    private void createSubscription(Shop shop) {
        Subscription subscription = Subscription.builder()
            .shop(shop)
            .status(FREE)
            .build();
        subscriptionRepository.save(subscription);
    }

    @Transactional
    public AuthDto.LogInResponse login(@RequestBody AuthDto.LogInRequest request) {

        UsernamePasswordAuthenticationToken authenticationToken = request.toAuthentication();

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        TokenDto token = tokenProvider.generateTokenDto(authentication).toTokenResponseDto();
        Shop shop = shopRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new CustomException(TARGET_SHOP_NOT_FOUND));
        AuthDto.LogInResponse response = AuthDto.LogInResponse.toLogInResponseDto(shop.getName(),
            shop.getAdminCode(), token);

        redisRepository.setValues(REFRESH_TOKEN_PREFIX + authentication.getName() + " " + response.getRefreshToken(),
            response.getRefreshToken(),
            Duration.ofMillis(refreshTokenExpirationMillis));

        shop.updateLastLoginAt();

        return response;
    }

    @Transactional
    public AuthDto.ReissueResponse reissue(AuthDto.ReissueRequest request) {
        if (!tokenProvider.validateToken(request.getRefreshToken())) {
            throw new CustomException(INVALID_REFRESH_TOKEN);
        }

        Authentication authentication = tokenProvider.getAuthentication(request.getAccessToken());

        String redisKey = REFRESH_TOKEN_PREFIX + authentication.getName() + " " + request.getRefreshToken();
        String refreshToken = redisRepository.getValues(redisKey);

        if (!refreshToken.equals(request.getRefreshToken())) {
            throw new CustomException(INVALID_REFRESH_TOKEN);
        }

        AuthDto.ReissueResponse response = tokenProvider.generateTokenDto(authentication).toReissueResponseDto();

        redisRepository.deleteValues(redisKey);
        redisRepository.setValues(REFRESH_TOKEN_PREFIX + authentication.getName() + " " + response.getRefreshToken(),
            response.getRefreshToken(),
            Duration.ofMillis(refreshTokenExpirationMillis));

        return response;
    }

    @Transactional
    public void unregister() {
        shopRepository.deleteById(SecurityUtil.getCurrentShopId());
    }
}
