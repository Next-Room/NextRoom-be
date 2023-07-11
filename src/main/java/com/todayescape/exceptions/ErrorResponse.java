package com.todayescape.exceptions;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Getter
@Builder
public class ErrorResponse {
    private final int code;
    private final String message;

    public static ResponseEntity<ErrorResponse> toResponseEntity(HttpStatus status, String message) {
        return ResponseEntity
                .status(status)
                .body(ErrorResponse.builder()
                        .code(status.value())
                        .message(message).build());
    }
}
