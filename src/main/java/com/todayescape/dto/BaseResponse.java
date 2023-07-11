package com.todayescape.dto;

import com.todayescape.exceptions.StatusCode;
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
