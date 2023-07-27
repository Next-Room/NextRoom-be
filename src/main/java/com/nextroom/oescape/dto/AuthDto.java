package com.nextroom.oescape.dto;

import java.util.Collections;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.nextroom.oescape.domain.Authority;
import com.nextroom.oescape.domain.Shop;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class AuthDto {
    private static final String ADMIN_CODE_REGEX = "[0-9]{5}";

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor(force = true)
    public static class SignUpRequestDto {
        @NotBlank(message = "관리자 코드는 필수 입력 사항입니다.")
        @Pattern(regexp = ADMIN_CODE_REGEX, message = "관리자 코드는 5자리 숫자(0~9)만 허용됩니다.")
        private final String adminCode;
        @Setter
        private String password;

        public Shop toShop(PasswordEncoder passwordEncoder) {
            return Shop.builder()
                .adminCode(this.adminCode)
                .password(passwordEncoder.encode(this.password))
                .authority(Authority.ROLE_USER)
                .build();
        }
    }

    @Getter
    @Builder
    public static class SignUpResponseDto {
        private String adminCode;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor(force = true)
    public static class LogInRequestDto {
        @NotBlank(message = "관리자 코드가 비어 있습니다.")
        @Pattern(regexp = ADMIN_CODE_REGEX, message = "관리자 코드는 5자리 숫자입니다.")
        private final String adminCode;
        @Setter
        private String password;

        public UsernamePasswordAuthenticationToken toAuthentication() {
            return new UsernamePasswordAuthenticationToken(this.adminCode, this.password,
                Collections.singleton(new SimpleGrantedAuthority(Authority.ROLE_USER.toString()))
            );
        }
    }

    @Getter
    @Builder
    public static class LogInResponseDto {
        private String grantType;
        private String accessToken;
        private long accessTokenExpiresIn;
        private String refreshToken;
    }

    @Getter
    @Builder
    public static class ReissueRequestDto {
        private String accessToken;
        private String refreshToken;
    }

    @Getter
    @Builder
    public static class ReissueResponseDto {
        private String grantType;
        private String accessToken;
        private long accessTokenExpiresIn;
        private String refreshToken;
    }
}
