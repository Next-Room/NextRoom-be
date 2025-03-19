package com.nextroom.nextRoomServer.controller;

import static com.nextroom.nextRoomServer.exceptions.StatusCode.OK;

import com.nextroom.nextRoomServer.dto.AuthDto;
import com.nextroom.nextRoomServer.dto.BaseResponse;
import com.nextroom.nextRoomServer.dto.DataResponse;
import com.nextroom.nextRoomServer.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
            @ApiResponse(responseCode = "403", description = "Unauthorized / Invalid Token", content = @Content)
        }
    )
    @PostMapping("/signup")
    public ResponseEntity<DataResponse<AuthDto.SignUpResponseDto>> signUp(@RequestBody @Valid AuthDto.SignUpRequestDto request) {
        return ResponseEntity.ok(new DataResponse<>(OK, authService.signUp(request)));
    }

    @Operation(
        summary = "로그인",
        responses = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "403", description = "Unauthorized / Invalid Token", content = @Content)
        }
    )
    @PostMapping("/login")
    public ResponseEntity<DataResponse<AuthDto.LogInResponseDto>> logIn(@RequestBody @Valid AuthDto.LogInRequestDto request) {
        return ResponseEntity.ok(new DataResponse<>(OK, authService.login(request)));
    }

    @Operation(
            summary = "구글 로그인(웹용)",
            description = "request: code / isComplete(넥룸 가입 절차 완료 여부)",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "403", description = "Unauthorized / Invalid Token", content = @Content)
            }
    )
    @GetMapping("/login/google/callback")
    public ResponseEntity<DataResponse<AuthDto.LogInResponseDto>> googleLogIn(AuthDto.GoogleLogInRequestDto request) {
        return ResponseEntity.ok(new DataResponse<>(OK, authService.googleLogin(request)));
    }

    @Operation(
            summary = "구글 로그인(앱용)",
            description = "request: idToken / isComplete(넥룸 가입 절차 완료 여부)",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "403", description = "Unauthorized / Invalid Token", content = @Content)
            }
    )
    @PostMapping("/login/google/app")
    public ResponseEntity<DataResponse<AuthDto.LogInResponseDto>> googleLogInApp(@RequestBody AuthDto.GoogleLogInRequestDto request) {
        return ResponseEntity.ok(new DataResponse<>(OK, authService.googleLogin(request)));
    }

    @Operation(
            summary = "넥스트룸 가입 절차",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "401", description = "TOKEN_UNAUTHORIZED", content = @Content),
                    @ApiResponse(responseCode = "404", description = "TARGET_SHOP_NOT_FOUND", content = @Content)
            }
    )
    @PutMapping("/shop")
    public ResponseEntity<DataResponse<AuthDto.ShopUpdateResponseDto>> updateShopInfo(@RequestBody @Valid AuthDto.ShopUpdateRequestDto request) {
        return ResponseEntity.ok(new DataResponse<>(OK, authService.updateShopInfo(request)));
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

    @Operation(
        summary = "회원탈퇴",
        responses = {
            @ApiResponse(responseCode = "200", description = "OK"),
        }
    )
    @DeleteMapping("/unregister")
    public ResponseEntity<BaseResponse> unregister() {
        authService.unregister();
        return ResponseEntity.ok(new BaseResponse(OK));
    }
}
