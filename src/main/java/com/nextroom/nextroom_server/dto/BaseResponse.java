package com.nextroom.nextroom_server.dto;

import com.nextroom.nextroom_server.exceptions.StatusCode;

import lombok.Getter;

@Getter
public class BaseResponse {
    private final int code;
    private final String message;

    public BaseResponse(StatusCode statusCode) {
        this.code = statusCode.getCode().value();
        this.message = statusCode.getMessage();
    }
}
