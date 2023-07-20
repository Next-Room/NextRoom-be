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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Auth")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @Operation(
        summary = "회원가입",
        responses = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "403", description = "Unauthorized / Invalid Token")
        }
    )
    @PostMapping("/signup")
    public ResponseEntity<BaseResponse> signUp(@RequestBody AuthDto.SignUpRequestDto request) {
        request.setPassword(request.getAdminCode());
        return ResponseEntity.ok(new DataResponse<>(OK, authService.signUp(request)));
    }

    @Operation(
        summary = "로그인",
        responses = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "403", description = "Unauthorized / Invalid Token")
        }
    )
    @PostMapping("/login")
    public ResponseEntity<BaseResponse> logIn(@RequestBody AuthDto.LogInRequestDto request) {
        request.setPassword(request.getAdminCode());
        return ResponseEntity.ok(new DataResponse<>(OK, authService.login(request)));
    }

    @Operation(
        summary = "토큰 재발급",
        responses = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "403", description = "Unauthorized / Invalid Token")
        }
    )
    @PostMapping("/reissue")
    public ResponseEntity<BaseResponse> reissue(@RequestBody AuthDto.ReissueRequestDto request) {
        return ResponseEntity.ok(new DataResponse<>(OK, authService.reissue(request)));
    }
}
