package com.nextroom.nextRoomServer.dto;

import java.util.Collections;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.nextroom.nextRoomServer.domain.Authority;
import com.nextroom.nextRoomServer.domain.Shop;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static com.nextroom.nextRoomServer.util.Timestamped.dateTimeFormatter;

public class AuthDto {
    private static final String ADMIN_CODE_REGEX = "[0-9]{5}";
    private static final String PASSWORD_CONDITION_MIN_LENGTH_REGEX = ".{8,}";
    private static final String PASSWORD_CONDITION_LOWER_CASE_REGEX = ".*[a-z].*";
    private static final String PASSWORD_CONDITION_UPPER_CASE_REGEX = ".*[A-Z].*";
    private static final String PASSWORD_CONDITION_NUMBER_REGEX = ".*[0-9].*";
    private static final String PASSWORD_CONDITION_SPECIAL_CHARACTER_REGEX = ".*[!@#$%^&*()].*";
    private static final String NO_NAME = "오픈 예정 매장";

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor(force = true)
    public static class SignUpRequestDto {
        @NotBlank(message = "이메일을 입력해 주세요.")
        @Email(message = "이메일 형식이 올바르지 않습니다.")
        private String email;
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
        private Integer type;
        @NotNull(message = "매장 오픈 여부를 입력해 주세요.")
        private Boolean isNotOpened;

        public Shop toShop(PasswordEncoder passwordEncoder, String adminCode) {
            String name = this.isNotOpened ? NO_NAME : this.name;
            String comment = this.isNotOpened ? this.name : null;

            return Shop.builder()
                .email(this.email)
                .adminCode(adminCode)
                .password(passwordEncoder.encode(this.password))
                .name(name)
                .type(this.type)
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

        public static AuthDto.SignUpResponseDto toSignUpResponseDto(Shop shop) {
            return SignUpResponseDto.builder()
                    .email(shop.getEmail())
                    .name(shop.getName())
                    .adminCode(shop.getAdminCode())
                    .createdAt(dateTimeFormatter(shop.getCreatedAt()))
                    .modifiedAt(dateTimeFormatter(shop.getModifiedAt())).build();
        }
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor(force = true)
    public static class LogInRequestDto {
        @NotBlank(message = "이메일을 입력해 주세요.")
        @Email(message = "이메일 형식이 올바르지 않습니다.")
        private String email;
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
        @NotBlank
        private String shopName;
        @NotBlank
        private String adminCode;
        @NotBlank
        private String grantType;
        @NotBlank
        private String accessToken;
        @NotNull
        private long accessTokenExpiresIn;
        @NotBlank
        private String refreshToken;

        @NotNull
        @Schema(description = "넥스트룸 가입 절차 완료 여부")
        private Boolean isComplete;

        public static AuthDto.LogInResponseDto toLogInResponseDto(Shop shop, TokenDto tokenDto) {
            return LogInResponseDto.builder()
                    .isComplete(shop.isCompleteSignUp())
                    .shopName(shop.getName())
                    .adminCode(shop.getAdminCode())
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
    public static class ShopUpdateRequestDto {
        @NotBlank(message = "매장명을 입력해 주세요.")
        @Schema(description = "매장명")
        private String name;
        @NotBlank(message = "가입 경로를 입력해 주세요.")
        @Schema(description = "가입 경로(ex. 네이버 검색)")
        private String signupSource;
        @Schema(description = "가입 이유(ex. 운영 중 매장 도입)")
        private String comment;
        @Schema(description = "가입 경로 타입 (1: 웹(홈페이지)에서 PC로 들어온 유저\n" +
                "2: 웹(홈페이지)에서 모바일로 들어온 유저\n" +
                "3: 앱에서 들어온 유저)")
        private Integer type;
        @Schema(description = "업데이트 소식 수신 동의 여부")
        private Boolean adsConsent;
    }

    @Getter
    @Builder
    public static class ShopUpdateResponseDto {
        @NotNull
        @Schema(description = "넥스트룸 가입 절차 완료 여부")
        private Boolean isComplete;
        @NotBlank
        @Schema(description = "매장명")
        private String shopName;
        @NotBlank
        @Schema(description = "관리자 코드")
        private String adminCode;

        public static AuthDto.ShopUpdateResponseDto toShopUpdateResponseDto(Shop shop) {
            return ShopUpdateResponseDto.builder()
                    .isComplete(shop.isCompleteSignUp())
                    .shopName(shop.getName())
                    .adminCode(shop.getAdminCode())
                    .build();
        }
    }

    @Getter
    @Setter
    public static class GoogleLogInRequestDto {
        private String code;
        @NotBlank(message = "ID TOKEN IS NOT NULL")
        private String idToken;
        public boolean isCode() {
            return this.code != null;
        }
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor(force = true)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class GoogleTokenResponseDto {
        @JsonProperty("access_token")
        private String accessToken;

        @JsonProperty("id_token")
        private String idToken;

        @JsonProperty("token_type")
        private String tokenType;

        @JsonProperty("expires_in")
        private String expiresIn;

        @JsonProperty("scope")
        private String scope;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor(force = true)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class GoogleInfoResponseDto {
        @JsonProperty("id")
        private String id;

        @JsonProperty("email")
        private String email;

        public static AuthDto.GoogleInfoResponseDto toGoogleInfoResponseDto(String id, String email) {
            return GoogleInfoResponseDto.builder()
                    .id(id)
                    .email(email)
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

        public static AuthDto.ReissueResponseDto toReissueResponseDto(TokenDto tokenDto) {
            return ReissueResponseDto.builder()
                    .grantType(tokenDto.getGrantType())
                    .accessToken(tokenDto.getAccessToken())
                    .accessTokenExpiresIn(tokenDto.getAccessTokenExpiresIn())
                    .refreshToken(tokenDto.getRefreshToken())
                    .build();
        }
    }
}
