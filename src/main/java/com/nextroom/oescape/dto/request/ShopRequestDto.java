package com.nextroom.oescape.dto.request;

import java.util.Collections;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.nextroom.oescape.domain.Authority;
import com.nextroom.oescape.domain.Shop;

import lombok.Getter;

public class ShopRequestDto {
    @Getter
    private String adminCode;
    @Getter
    private String password;

    public Shop toShop(PasswordEncoder passwordEncoder) {
        return Shop.builder()
            .adminCode(this.adminCode)
            .password(passwordEncoder.encode(this.password))
            .authority(Authority.ROLE_USER)
            .build();
    }

    public UsernamePasswordAuthenticationToken toAuthentication() {
        return new UsernamePasswordAuthenticationToken(this.adminCode, this.password,
            Collections.singleton(new SimpleGrantedAuthority(Authority.ROLE_USER.toString()))
        );
    }
}
