package com.nextroom.oescape.dto.response;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class TokenResponseDto {
    private String grantType;
    private String accessToken;
    private long accessTokenExpiresIn;
    private String refreshToken;
}
