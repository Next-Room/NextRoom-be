package com.todayescape.dto;

import com.todayescape.exceptions.StatusCode;
import lombok.Getter;

@Getter
public class DataResponse<T> extends BaseResponse{
    private final T data;

    public DataResponse(StatusCode statusCode, T data) {
        super(statusCode);
        this.data = data;
    }
}
