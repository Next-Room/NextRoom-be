package com.nextroom.oescape.dto.request;

import java.util.Collections;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.nextroom.oescape.domain.Authority;
import com.nextroom.oescape.domain.Shop;

import lombok.Getter;

public class ShopRequestDto {
    @Getter
    private String adminCode;

    public Shop toShop() {
        return Shop.builder()
            .adminCode(this.adminCode).name("tmp") // TODO 초기 업체명 설정 (prototype)
            .authority(Authority.ROLE_USER)
            .build();
    }

    public UsernamePasswordAuthenticationToken toAuthentication() {
        return new UsernamePasswordAuthenticationToken(this.adminCode, this.adminCode,
            Collections.singleton(new SimpleGrantedAuthority(Authority.ROLE_USER.toString()))
        );
    }
}
