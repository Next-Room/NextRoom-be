package com.nextroom.nextRoomServer.exceptions;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {

    private final StatusCode statusCode;
    private final String message;

    public CustomException(StatusCode statusCode) {
        this.statusCode = statusCode;
        this.message = statusCode.getMessage();
    }

    public CustomException(StatusCode statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }
}
