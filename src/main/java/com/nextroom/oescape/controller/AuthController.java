package com.nextroom.oescape.controller;

import static com.nextroom.oescape.exceptions.StatusCode.*;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nextroom.oescape.dto.AuthDto;
import com.nextroom.oescape.dto.BaseResponse;
import com.nextroom.oescape.dto.DataResponse;
import com.nextroom.oescape.service.AuthService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<BaseResponse> signUp(@RequestBody AuthDto.SignUpRequestDto request) {
        request.setPassword(request.getAdminCode());
        return ResponseEntity.ok(new DataResponse<>(OK, authService.signUp(request)));
    }

    @PostMapping("/login")
    public ResponseEntity<BaseResponse> logIn(@RequestBody AuthDto.LogInRequestDto request) {
        request.setPassword(request.getAdminCode());
        return ResponseEntity.ok(new DataResponse<>(OK, authService.login(request)));
    }

    @PostMapping("/reissue")
    public ResponseEntity<BaseResponse> reissue(@RequestBody AuthDto.ReissueRequestDto request) {
        return ResponseEntity.ok(new DataResponse<>(OK, authService.reissue(request)));
    }
}
