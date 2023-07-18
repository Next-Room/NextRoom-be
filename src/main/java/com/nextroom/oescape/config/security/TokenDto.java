package com.nextroom.oescape.config.security;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class TokenDto {
    private String grantType;
    private String accessToken;
    private long accessTokenExpiresIn;
    private String refreshToken;
}
