package com.nextroom.nextRoomServer.service;

import static com.nextroom.nextRoomServer.enums.UserStatus.*;
import static com.nextroom.nextRoomServer.exceptions.StatusCode.*;

import java.time.Duration;
import java.util.stream.Collectors;

import com.nextroom.nextRoomServer.domain.Authority;
import com.nextroom.nextRoomServer.util.oauth2.GoogleClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
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
    private final GoogleClient googleClient;

    @Value("${jwt.refresh-token-expiration-millis}")
    private long refreshTokenExpirationMillis;
    private static final String REFRESH_TOKEN_PREFIX = "RefreshToken ";

    @Transactional
    public AuthDto.SignUpResponseDto signUp(AuthDto.SignUpRequestDto request) {
        shopRepository.findByEmailAndGoogleSubIsNull(request.getEmail())
                .orElseThrow(() -> new CustomException(SHOP_ALREADY_EXIST));
        Shop shop = shopRepository.save(request.toShop(passwordEncoder, createAdminCode()));
        createSubscription(shop);

        return AuthDto.SignUpResponseDto.toSignUpResponseDto(shop);
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
    public AuthDto.LogInResponseDto login(@RequestBody AuthDto.LogInRequestDto request) {

        UsernamePasswordAuthenticationToken authenticationToken = request.toAuthentication();
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        TokenDto token = tokenProvider.generateTokenDto(authentication.getName(), authorities).toTokenResponseDto();

        Shop shop = shopRepository.findByEmailAndGoogleSubIsNull(request.getEmail())
            .orElseThrow(() -> new CustomException(TARGET_SHOP_NOT_FOUND));
        AuthDto.LogInResponseDto response = AuthDto.LogInResponseDto.toLogInResponseDto(shop, token);

        redisRepository.setValues(REFRESH_TOKEN_PREFIX + authentication.getName() + " " + response.getRefreshToken(),
            response.getRefreshToken(),
            Duration.ofMillis(refreshTokenExpirationMillis));

        shop.updateLastLoginAt();

        return response;
    }

    @Transactional
    public AuthDto.LogInResponseDto googleLogin(AuthDto.GoogleLogInRequestDto request) {
        AuthDto.GoogleInfoResponseDto userInfo = googleClient.getUserInfo(request);
        Shop shop = this.save(userInfo);
        if (shop.isNotCompleteSignUp()) {
            return AuthDto.LogInResponseDto.toShopInfoResponseDto(shop);
        }

        String stringShopId = shop.getId().toString();
        TokenDto token = tokenProvider.generateTokenDto(stringShopId, shop.getAuthority().toString());
        redisRepository.setValues(REFRESH_TOKEN_PREFIX + stringShopId + " " + token.getRefreshToken(),
                token.getRefreshToken(),
                Duration.ofMillis(refreshTokenExpirationMillis));
        shop.updateLastLoginAt();

        return AuthDto.LogInResponseDto.toLogInResponseDto(shop, token);
    }

    @Transactional
    public AuthDto.ReissueResponseDto reissue(AuthDto.ReissueRequestDto request) {
        if (!tokenProvider.validateToken(request.getRefreshToken())) {
            throw new CustomException(INVALID_REFRESH_TOKEN);
        }

        Authentication authentication = tokenProvider.getAuthentication(request.getAccessToken());

        String redisKey = REFRESH_TOKEN_PREFIX + authentication.getName() + " " + request.getRefreshToken();
        String refreshToken = redisRepository.getValues(redisKey);

        if (!refreshToken.equals(request.getRefreshToken())) {
            throw new CustomException(INVALID_REFRESH_TOKEN);
        }

        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        AuthDto.ReissueResponseDto response = tokenProvider.generateTokenDto(authentication.getName(), authorities).toReissueResponseDto();

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

    private Shop save(AuthDto.GoogleInfoResponseDto userInfo) {
        String email = userInfo.getEmail();
        String sub = userInfo.getId();

        return shopRepository.findByEmailAndGoogleSub(email, sub)
                .orElseGet(() -> {
                    Shop newShop = Shop.builder()
                            .email(email)
                            .googleSub(sub)
                            .authority(Authority.ROLE_USER)
                            .adminCode(createAdminCode())
                            .build();
                    newShop = shopRepository.save(newShop);
                    createSubscription(newShop);
                    return newShop;
                });
    }
}
