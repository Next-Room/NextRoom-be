package com.nextroom.nextRoomServer.exceptions;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum StatusCode {

    /**
     * 400 Bad Request
     */
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
    AUTH_BAD_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
    SHOP_ALREADY_EXIST(HttpStatus.BAD_REQUEST, "업체가 이미 존재합니다."),
    INVALID_TOKEN(HttpStatus.BAD_REQUEST, "잘못된 토큰입니다."),
    UNSUPPORTED_TOKEN(HttpStatus.BAD_REQUEST, "지원하지 않는 토큰 형식입니다."),
    INVALID_TOKEN_SIGNATURE(HttpStatus.BAD_REQUEST, "잘못된 토큰 서명입니다."),

    /**
     * 401 Unauthorized
     */
    TOKEN_UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "권한 정보가 잘못된 토큰입니다."),
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "만료된 토큰입니다."),
    SHOP_IS_LOG_OUT(HttpStatus.UNAUTHORIZED, "로그아웃 된 사용자입니다."),

    /**
     * 403 FORBIDDEN
     */
    INVALID_REFRESH_TOKEN(HttpStatus.FORBIDDEN, "Refresh token 이 유효하지 않습니다."),
    NOT_PERMITTED(HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),

    /**
     * 404 Not Found
     */
    NOT_FOUND(HttpStatus.NOT_FOUND, "리소스를 찾을 수 없습니다."),
    THEME_NOT_FOUND(HttpStatus.NOT_FOUND, "등록된 테마가 없습니다."),
    HINT_NOT_FOUND(HttpStatus.NOT_FOUND, "등록된 힌트가 없습니다."),
    TARGET_THEME_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 테마입니다."),
    TARGET_HINT_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 힌트입니다."),
    TARGET_SHOP_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 업체입니다."),

    /**
     * 409 Conflict
     */
    CONFLICT(HttpStatus.CONFLICT, "중복된 리소스 입니다."),
    HINT_CODE_CONFLICT(HttpStatus.CONFLICT, "테마 내 같은 힌트코드를 가진 힌트가 존재합니다."),

    /**
     * 2xx OK
     */
    OK(HttpStatus.OK, "성공");

    private final HttpStatus code;
    private final String message;
}
