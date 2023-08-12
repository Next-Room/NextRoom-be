package com.nextroom.nextRoomServer.dto;

import com.nextroom.nextRoomServer.exceptions.StatusCode;

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
