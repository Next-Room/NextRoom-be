package com.nextroom.nextRoomServer.service;

import static com.nextroom.nextRoomServer.enums.UserStatus.*;
import static com.nextroom.nextRoomServer.exceptions.StatusCode.*;

import java.time.Duration;
import java.util.Optional;
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
        checkDuplicatedEmail(request.getEmail());
        Shop shop = shopRepository.save(request.toShop(passwordEncoder, createAdminCode()));
        createSubscription(shop);

        return AuthDto.SignUpResponseDto.toSignUpResponseDto(shop);
    }

    @Transactional
    public AuthDto.LogInResponseDto login(AuthDto.LogInRequestDto request) {
        UsernamePasswordAuthenticationToken authenticationToken = request.toAuthentication();
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        TokenDto token = this.generateAndSaveToken(authentication.getName(), getAuthorities(authentication));

        Shop shop = shopRepository.findByEmailAndGoogleSubIsNull(request.getEmail())
            .orElseThrow(() -> new CustomException(TARGET_SHOP_NOT_FOUND));
        shop.updateLastLoginAt();

        return AuthDto.LogInResponseDto.toLogInResponseDto(shop, token);
    }

    @Transactional
    public AuthDto.LogInResponseDto googleLogin(AuthDto.GoogleLogInRequestDto request) {
        AuthDto.GoogleInfoResponseDto userInfo = googleClient.getUserInfo(request);
        Shop shop = this.saveOrGet(userInfo);
        shop.updateLastLoginAt();
        TokenDto token = generateAndSaveToken(shop.getId().toString(), shop.getAuthority().toString());

        return AuthDto.LogInResponseDto.toLogInResponseDto(shop, token);
    }

    @Transactional
    public AuthDto.ShopUpdateResponseDto updateShopInfo(AuthDto.ShopUpdateRequestDto request) {
        Shop shop = getShop(SecurityUtil.getCurrentShopId());
        shop.updateShopInfo(request);

        return AuthDto.ShopUpdateResponseDto.toShopUpdateResponseDto(shop);
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

        TokenDto token = this.generateAndSaveToken(authentication.getName(), getAuthorities(authentication));
        redisRepository.deleteValues(redisKey);

        return AuthDto.ReissueResponseDto.toReissueResponseDto(token);
    }

    @Transactional
    public void unregister() {
        shopRepository.deleteById(SecurityUtil.getCurrentShopId());
    }

    private void checkDuplicatedEmail(String email) {
        Optional<Shop> shop = shopRepository.findByEmailAndGoogleSubIsNull(email);
        if (shop.isPresent()) {
            throw new CustomException(SHOP_ALREADY_EXIST);
        }
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

    private Shop getShop(Long shopId) {
        return shopRepository.findById(shopId)
                .orElseThrow(() -> new CustomException(TARGET_SHOP_NOT_FOUND));
    }

    private String getAuthorities(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
    }

    private Shop saveOrGet(AuthDto.GoogleInfoResponseDto userInfo) {
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

    private TokenDto generateAndSaveToken(String stringShopId, String authorities) {
        TokenDto token = tokenProvider.generateTokenDto(stringShopId, authorities).toTokenResponseDto();
        redisRepository.setValues(
                REFRESH_TOKEN_PREFIX + stringShopId + " " + token.getRefreshToken(),
                token.getRefreshToken(),
                Duration.ofMillis(refreshTokenExpirationMillis));
        return token;
    }
}
