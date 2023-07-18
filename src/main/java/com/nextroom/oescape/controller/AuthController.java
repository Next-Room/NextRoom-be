package com.nextroom.oescape.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nextroom.oescape.config.security.TokenDto;
import com.nextroom.oescape.domain.Shop;
import com.nextroom.oescape.dto.request.ShopRequestDto;
import com.nextroom.oescape.dto.request.TokenRequestDto;
import com.nextroom.oescape.dto.response.ShopResponseDto;
import com.nextroom.oescape.service.AuthService;

import lombok.RequiredArgsConstructor;

// TODO 1st: 인증/인가 정상 실행 확인
// TODO 2nd: 응답 형식 우리 프로젝트 컨벤션에 맞게 설정
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<ShopResponseDto> signup(@RequestBody ShopRequestDto shopRequestDto) {
        return ResponseEntity.ok(authService.signup(shopRequestDto));
    }

    @PostMapping("/login")
    public ResponseEntity<TokenDto> login(@RequestBody ShopRequestDto shopRequestDto) {
        return ResponseEntity.ok(authService.login(shopRequestDto));
    }

    @PostMapping("/reissue")
    public ResponseEntity<TokenDto> reissue(@RequestBody TokenRequestDto tokenRequestDto) {
        return ResponseEntity.ok(authService.reissue(tokenRequestDto));
    }
}
