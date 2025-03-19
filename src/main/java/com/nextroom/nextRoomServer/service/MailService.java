package com.nextroom.nextRoomServer.service;

import static com.nextroom.nextRoomServer.exceptions.StatusCode.*;

import java.time.Duration;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.nextroom.nextRoomServer.domain.Shop;
import com.nextroom.nextRoomServer.dto.MailDto;
import com.nextroom.nextRoomServer.exceptions.CustomException;
import com.nextroom.nextRoomServer.repository.RedisRepository;
import com.nextroom.nextRoomServer.repository.ShopRepository;
import com.nextroom.nextRoomServer.util.RandomCodeGenerator;
import com.nextroom.nextRoomServer.util.email.MailSender;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MailService {

    private final ShopRepository shopRepository;
    private final RedisRepository redisRepository;

    @Value("${spring.mail.auth-code-expiration-millis}")
    private long authCodeExpirationMillis;
    private static final String AUTH_CODE_PREFIX = "AuthCode ";
    private final MailSender mailSender;
    private final RandomCodeGenerator randomCodeGenerator;

    @Transactional
    public void sendCodeToEmail(MailDto.SendRequestDto request) {
        String toEmail = request.getEmail();
        this.checkDuplicatedEmail(toEmail);
        String authCode = randomCodeGenerator.createCode(6);
        mailSender.sendEmail(toEmail, authCode);
        // 이메일 인증 요청 시 인증 번호 Redis에 저장 ( key = "AuthCode " + Email / value = AuthCode )
        redisRepository.setValues(AUTH_CODE_PREFIX + toEmail, authCode,
            Duration.ofMillis(this.authCodeExpirationMillis));
    }

    private void checkDuplicatedEmail(String email) {
        Optional<Shop> shop = shopRepository.findByEmailAndGoogleSubIsNull(email);
        if (shop.isPresent()) {
            throw new CustomException(SHOP_ALREADY_EXIST);
        }
    }

    @Transactional
    public void verifiedCode(MailDto.VerifyRequestDto request) {
        String email = request.getEmail();
        this.checkDuplicatedEmail(email);
        String redisAuthCode = redisRepository.getValues(AUTH_CODE_PREFIX + email);

        if (isNotVerifiedCode(request.getCode(), redisAuthCode)) {
            throw new CustomException(INVALID_CODE);
        }

        redisRepository.deleteValues(AUTH_CODE_PREFIX + email);
    }

    private boolean isNotVerifiedCode(String authCode, String redisAuthCode) {
        return redisRepository.isNotExistsValue(redisAuthCode) || !redisAuthCode.equals(authCode);
    }

}
