package com.nextroom.nextRoomServer.dto;

import java.util.Collections;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.nextroom.nextRoomServer.domain.Authority;
import com.nextroom.nextRoomServer.domain.Shop;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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
        @NotBlank(message = "이메일을 입력해 주세요.")
        @Email(message = "이메일 형식이 올바르지 않습니다.")
        private final String email;
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
        @NotNull(message = "매장 오픈 여부를 입력해 주세요.")
        private Boolean isNotOpened;

        public Shop toShop(PasswordEncoder passwordEncoder) {
            String name = this.isNotOpened ? "오픈 예정 매장" : this.name;
            String comment = this.isNotOpened ? this.name : null;

            return Shop.builder()
                .email(this.email)
                .adminCode("00000")
                .password(passwordEncoder.encode(this.password))
                .name(name)
                .comment(comment)
                .authority(Authority.ROLE_USER)
                .build();
        }
    }

    @Getter
    @Builder
    public static class SignUpResponseDto {
        private String email;
        private String name;
        private String adminCode;
        private String createdAt;
        private String modifiedAt;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor(force = true)
    public static class LogInRequestDto {
        @NotBlank(message = "이메일을 입력해 주세요.")
        @Email(message = "이메일 형식이 올바르지 않습니다.")
        private final String email;
        @Setter
        @NotBlank(message = "비밀번호를 입력해 주세요.")
        private String password;

        public UsernamePasswordAuthenticationToken toAuthentication() {
            return new UsernamePasswordAuthenticationToken(this.email, this.password,
                Collections.singleton(new SimpleGrantedAuthority(Authority.ROLE_USER.toString()))
            );
        }
    }

    @Getter
    @Builder
    public static class LogInResponseDto {
        private String shopName;
        private String adminCode;
        private String grantType;
        private String accessToken;
        private long accessTokenExpiresIn;
        private String refreshToken;

        public static AuthDto.LogInResponseDto toLogInResponseDto(String shopName, String adminCode,
            TokenDto tokenDto) {
            return new LogInResponseDtoBuilder()
                .shopName(shopName)
                .adminCode(adminCode)
                .grantType(tokenDto.getGrantType())
                .accessToken(tokenDto.getAccessToken())
                .accessTokenExpiresIn(tokenDto.getAccessTokenExpiresIn())
                .refreshToken(tokenDto.getRefreshToken())
                .build();
        }
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor(force = true)
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
