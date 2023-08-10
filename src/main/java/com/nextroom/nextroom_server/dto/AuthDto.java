package com.nextroom.nextroom_server.dto;

import java.util.Collections;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.nextroom.nextroom_server.domain.Authority;
import com.nextroom.nextroom_server.domain.Shop;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class AuthDto {
    private static final String ADMIN_CODE_REGEX = "[0-9]{5}";
    private static final String PASSWORD_CONDITION_MIN_LENGTH_REGEX = ".{8,}";
    private static final String PASSWORD_CONDITION_LOWER_CASE_REGEX = ".*[a-z].*";
    private static final String PASSWORD_CONDITION_UPPER_CASE_REGEX = ".*[A-Z].*";
    private static final String PASSWORD_CONDITION_NUMBER_REGEX = ".*[0-9].*";
    private static final String PASSWORD_CONDITION_SPECIAL_CHARACTER_REGEX = ".*[!@#$%^&*()].*";

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor(force = true)
    public static class SignUpRequestDto {
        @NotBlank(message = "관리자 코드는 필수 입력 사항입니다.")
        @Pattern(regexp = ADMIN_CODE_REGEX, message = "관리자 코드는 5자리 숫자(0~9)만 허용됩니다.")
        private final String adminCode;
        @Setter
        @NotEmpty(message = "비밀번호를 입력해 주세요.")
        @Pattern(regexp = PASSWORD_CONDITION_MIN_LENGTH_REGEX, message = "비밀번호는 최소 8자리 이상이어야 합니다.")
        @Pattern(regexp = PASSWORD_CONDITION_LOWER_CASE_REGEX, message = "비밀번호에 영문 소문자를 최소 1개 이상 포함해야 합니다.")
        @Pattern(regexp = PASSWORD_CONDITION_UPPER_CASE_REGEX, message = "비밀번호에 영문 대문자를 최소 1개 이상 포함해야 합니다.")
        @Pattern(regexp = PASSWORD_CONDITION_NUMBER_REGEX, message = "비밀번호에 숫자(0-9)를 최소 1개 이상 포함해야 합니다.")
        @Pattern(regexp = PASSWORD_CONDITION_SPECIAL_CHARACTER_REGEX, message = "비밀번호에 특수문자(!, @, #, $, %, ^, &, *, (, ))를 최소 1개 이상 포함해야 합니다.")
        private String password;
        @NotBlank(message = "업체명을 입력해 주세요.")
        private String name;

        public Shop toShop(PasswordEncoder passwordEncoder) {
            return Shop.builder()
                .adminCode(this.adminCode)
                .password(passwordEncoder.encode(this.password))
                .name(this.name)
                .authority(Authority.ROLE_USER)
                .build();
        }
    }

    @Getter
    @Builder
    public static class SignUpResponseDto {
        private String adminCode;
        private String name;
        private String createdAt;
        private String modifiedAt;
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
        @NotEmpty(message = "비밀번호를 입력해 주세요.")
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
        private String shopName;
        private String grantType;
        private String accessToken;
        private long accessTokenExpiresIn;
        private String refreshToken;

        public static AuthDto.LogInResponseDto toLogInResponseDto(String shopName, TokenDto tokenDto) {
            return new LogInResponseDtoBuilder()
                .shopName(shopName)
                .grantType(tokenDto.getGrantType())
                .accessToken(tokenDto.getAccessToken())
                .accessTokenExpiresIn(tokenDto.getAccessTokenExpiresIn())
                .refreshToken(tokenDto.getRefreshToken())
                .build();
        }
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
