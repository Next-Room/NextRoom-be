package com.nextroom.oescape.dto;

import com.nextroom.oescape.exceptions.StatusCode;

import lombok.Getter;

@Getter
public class DataResponse<T> extends BaseResponse {
    private final T data;

    public DataResponse(StatusCode statusCode, T data) {
        super(statusCode);
        this.data = data;
    }
}
