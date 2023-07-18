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

    /**
     * 404 Not Found
     */
    NOT_FOUND(HttpStatus.NOT_FOUND, "리소스를 찾을 수 없습니다."),
    THEME_NOT_FOUNT(HttpStatus.NOT_FOUND, "등록된 테마가 없습니다."),

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
