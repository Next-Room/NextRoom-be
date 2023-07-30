package com.nextroom.oescape.exceptions;

import java.util.Objects;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = {CustomException.class})
    protected ResponseEntity<ErrorResponse> handleCustomException(CustomException e) {
        return ErrorResponse.toResponseEntity(e.getStatusCode().getCode(), e.getStatusCode().getMessage());
    }

    @ExceptionHandler(value = {MethodArgumentNotValidException.class})
    protected ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        return ErrorResponse.toResponseEntity((HttpStatus)e.getStatusCode(),
            Objects.requireNonNull(e.getBindingResult().getFieldError()).getDefaultMessage());
    }

    @ExceptionHandler(value = {HttpMessageNotReadableException.class})
    protected ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        return ErrorResponse.toResponseEntity(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(value = {io.jsonwebtoken.security.SecurityException.class})
    protected ResponseEntity<ErrorResponse> handleSecurityException(io.jsonwebtoken.security.SecurityException e) {
        return ErrorResponse.toResponseEntity(HttpStatus.BAD_REQUEST, "잘못된 JWT 서명입니다.");
    }

    @ExceptionHandler(value = {MalformedJwtException.class})
    protected ResponseEntity<ErrorResponse> handleMalformedJwtException(MalformedJwtException e) {
        return ErrorResponse.toResponseEntity(HttpStatus.BAD_REQUEST, "잘못된 JWT 서명입니다.");
    }

    @ExceptionHandler(value = {ExpiredJwtException.class})
    protected ResponseEntity<ErrorResponse> handleExpiredJwtException(ExpiredJwtException e) {
        return ErrorResponse.toResponseEntity(HttpStatus.BAD_REQUEST, "만료된 JWT 토큰입니다.");
    }

    @ExceptionHandler(value = {UnsupportedJwtException.class})
    protected ResponseEntity<ErrorResponse> handleUnsupportedJwtException(UnsupportedJwtException e) {
        return ErrorResponse.toResponseEntity(HttpStatus.BAD_REQUEST, "지원되지 않는 JWT 토큰입니다.");
    }

    @ExceptionHandler(value = {IllegalArgumentException.class})
    protected ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException e) {
        return ErrorResponse.toResponseEntity(HttpStatus.BAD_REQUEST, "JWT 토큰이 잘못되었습니다.");
    }

    @ExceptionHandler(value = {AuthenticationException.class})
    protected ResponseEntity<ErrorResponse> handleAuthenticationException(AuthenticationException e) {
        return ErrorResponse.toResponseEntity(HttpStatus.NOT_FOUND, "존재하지 않는 업체입니다.");
    }

}
