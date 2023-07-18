package com.nextroom.oescape.dto.request;

import lombok.Getter;

public class TokenRequestDto {
    @Getter
    private String accessToken;
    @Getter
    private String refreshToken;
}
