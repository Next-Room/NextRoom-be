package com.nextroom.nextRoomServer.controller;

import static com.nextroom.nextRoomServer.exceptions.StatusCode.*;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nextroom.nextRoomServer.dto.BaseResponse;
import com.nextroom.nextRoomServer.dto.MailDto;
import com.nextroom.nextRoomServer.service.MailService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Mail")
@RestController
@RequestMapping("/api/v1/email")
@RequiredArgsConstructor
public class MailController {
    private final MailService mailService;

    @Operation(
        summary = "이메일 전송",
        responses = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Unalbe to send email"),
            @ApiResponse(responseCode = "403", description = "Unauthorized / Invalid Token"),
            @ApiResponse(responseCode = "409", description = "Shop already exist")
        }
    )
    @PostMapping("/verification-requests")
    public ResponseEntity<BaseResponse> sendMessage(@RequestBody @Valid MailDto.SendRequest request) {
        mailService.sendCodeToEmail(request);
        return ResponseEntity.ok(new BaseResponse(OK));
    }

    @Operation(
        summary = "이메일 인증",
        responses = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "401", description = "Invalid email code"),
            @ApiResponse(responseCode = "403", description = "Unauthorized / Invalid Token"),
            @ApiResponse(responseCode = "409", description = "Shop already exist")
        }
    )
    @PostMapping("/verifications")
    public ResponseEntity<BaseResponse> sendMessage(@RequestBody @Valid MailDto.VerifyRequest request) {
        mailService.verifiedCode(request);
        return ResponseEntity.ok(new BaseResponse(OK));
    }
}
