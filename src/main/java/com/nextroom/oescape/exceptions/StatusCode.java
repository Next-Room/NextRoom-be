package com.nextroom.oescape.exceptions;

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

    /**
     * 401 Unauthorized
     */
    TOKEN_UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "권한 정보가 잘못된 토큰입니다."),
    SHOP_IS_LOG_OUT(HttpStatus.UNAUTHORIZED, "로그아웃 된 사용자입니다."),

    /**
     * 403 FORBIDDEN
     */
    INVALID_TOKEN(HttpStatus.FORBIDDEN, "토큰의 유저 정보가 일치하지 않습니다."),
    INVALID_REFRESH_TOKEN(HttpStatus.FORBIDDEN, "Refresh token 이 유효하지 않습니다."),

    /**
     * 404 Not Found
     */
    NOT_FOUND(HttpStatus.NOT_FOUND, "리소스를 찾을 수 없습니다."),
    THEME_NOT_FOUND(HttpStatus.NOT_FOUND, "등록된 테마가 없습니다."),

    /**
     * 409 Conflict
     */
    CONFLICT(HttpStatus.CONFLICT, "중복된 리소스 입니다."),

    /**
     * 2xx OK
     */
    OK(HttpStatus.OK, "성공");

    private final HttpStatus code;
    private final String message;
}
