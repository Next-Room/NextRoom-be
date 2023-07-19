package com.nextroom.oescape.controller;

import static com.nextroom.oescape.exceptions.StatusCode.*;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nextroom.oescape.domain.Shop;
import com.nextroom.oescape.dto.BaseResponse;
import com.nextroom.oescape.dto.DataResponse;
import com.nextroom.oescape.dto.response.TokenResponseDto;
import com.nextroom.oescape.dto.request.ShopRequestDto;
import com.nextroom.oescape.dto.request.TokenRequestDto;
import com.nextroom.oescape.dto.response.ShopResponseDto;
import com.nextroom.oescape.service.AuthService;

import lombok.RequiredArgsConstructor;

// TODO 2nd: 응답 형식 우리 프로젝트 컨벤션에 맞게 설정
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<BaseResponse> signup(@RequestBody ShopRequestDto shopRequestDto) {
        return ResponseEntity.ok(new DataResponse<>(OK, authService.signup(shopRequestDto)));
    }

    @PostMapping("/login")
    public ResponseEntity<BaseResponse> login(@RequestBody ShopRequestDto shopRequestDto) {
        return ResponseEntity.ok(new DataResponse<>(OK, authService.login(shopRequestDto)));
    }

    @PostMapping("/reissue")
    public ResponseEntity<BaseResponse> reissue(@RequestBody TokenRequestDto tokenRequestDto) {
        return ResponseEntity.ok(new DataResponse<>(OK, authService.reissue(tokenRequestDto)));
    }
}
