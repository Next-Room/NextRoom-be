package com.nextroom.nextRoomServer.service;

import static com.nextroom.nextRoomServer.enums.SubscriptionPlan.MINI;
import static com.nextroom.nextRoomServer.enums.UserStatus.FREE;
import static com.nextroom.nextRoomServer.exceptions.StatusCode.INVALID_REFRESH_TOKEN;
import static com.nextroom.nextRoomServer.exceptions.StatusCode.SHOP_ALREADY_EXIST;
import static com.nextroom.nextRoomServer.exceptions.StatusCode.SHOP_IS_LOG_OUT;
import static com.nextroom.nextRoomServer.exceptions.StatusCode.TARGET_SHOP_NOT_FOUND;
import static com.nextroom.nextRoomServer.util.Timestamped.dateTimeFormatter;

import com.nextroom.nextRoomServer.domain.RefreshToken;
import com.nextroom.nextRoomServer.domain.Shop;
import com.nextroom.nextRoomServer.domain.Subscription;
import com.nextroom.nextRoomServer.dto.AuthDto;
import com.nextroom.nextRoomServer.dto.TokenDto;
import com.nextroom.nextRoomServer.exceptions.CustomException;
import com.nextroom.nextRoomServer.repository.RefreshTokenRepository;
import com.nextroom.nextRoomServer.repository.ShopRepository;
import com.nextroom.nextRoomServer.repository.SubscriptionRepository;
import com.nextroom.nextRoomServer.security.SecurityUtil;
import com.nextroom.nextRoomServer.security.TokenProvider;
import com.nextroom.nextRoomServer.util.RandomCodeGenerator;
import com.nextroom.nextRoomServer.util.Timestamped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final ShopRepository shopRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final RandomCodeGenerator randomCodeGenerator;

    @Transactional
    public AuthDto.SignUpResponseDto signUp(AuthDto.SignUpRequestDto request) {
        if (shopRepository.existsByEmail(request.getEmail())) {
            throw new CustomException(SHOP_ALREADY_EXIST);
        }

        String adminCode = createAdminCode();
        Shop shop = shopRepository.save(request.toShop(passwordEncoder, adminCode));
        createSubscription(shop);

        return AuthDto.SignUpResponseDto.builder()
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
            .plan(MINI)
            .expiryDate(Timestamped.getToday().plusDays(30))
            .build();
        subscriptionRepository.save(subscription);
    }

    @Transactional
    public AuthDto.LogInResponseDto login(@RequestBody AuthDto.LogInRequestDto request) {

        UsernamePasswordAuthenticationToken authenticationToken = request.toAuthentication();

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        TokenDto token = tokenProvider.generateTokenDto(authentication).toTokenResponseDto();
        Shop shop = shopRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new CustomException(TARGET_SHOP_NOT_FOUND));
        AuthDto.LogInResponseDto response = AuthDto.LogInResponseDto.toLogInResponseDto(shop.getName(),
            shop.getAdminCode(), token);

        RefreshToken refreshToken = RefreshToken.builder()
            .key(authentication.getName())
            .value(response.getRefreshToken())
            .build();

        refreshTokenRepository.save(refreshToken);

        shop.updateLastLoginAt();

        return response;
    }

    @Transactional
    public AuthDto.ReissueResponseDto reissue(AuthDto.ReissueRequestDto request) {
        if (!tokenProvider.validateToken(request.getRefreshToken())) {
            throw new CustomException(INVALID_REFRESH_TOKEN);
        }

        Authentication authentication = tokenProvider.getAuthentication(request.getAccessToken());

        RefreshToken refreshToken = refreshTokenRepository.findByKey(authentication.getName())
            .orElseThrow(() -> new CustomException(SHOP_IS_LOG_OUT));

        if (!refreshToken.getValue().equals(request.getRefreshToken())) {
            throw new CustomException(INVALID_REFRESH_TOKEN);
        }

        AuthDto.ReissueResponseDto response = tokenProvider.generateTokenDto(authentication).toReissueResponseDto();

        RefreshToken newRefreshToken = refreshToken.updateValue(response.getRefreshToken());
        refreshTokenRepository.save(newRefreshToken);

        return response;
    }

    @Transactional
    public void unregister() {
        shopRepository.deleteById(SecurityUtil.getRequestedShopId());
    }
}
