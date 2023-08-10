package com.nextroom.nextroom_server.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class TokenDto {
    private String grantType;
    private String accessToken;
    private long accessTokenExpiresIn;
    private String refreshToken;

    public TokenDto toTokenResponseDto() {
        return new TokenDto.TokenDtoBuilder()
            .grantType(this.grantType)
            .accessToken(this.accessToken)
            .accessTokenExpiresIn(this.accessTokenExpiresIn)
            .refreshToken(this.refreshToken)
            .build();
    }

    public AuthDto.ReissueResponseDto toReissueResponseDto() {
        return new AuthDto.ReissueResponseDto.ReissueResponseDtoBuilder()
            .grantType(this.grantType)
            .accessToken(this.accessToken)
            .accessTokenExpiresIn(this.accessTokenExpiresIn)
            .refreshToken(this.refreshToken)
            .build();
    }
}
