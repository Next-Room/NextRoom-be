package com.nextroom.oescape.exceptions;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(value = {CustomException.class})
	protected ResponseEntity<ErrorResponse> handleCustomException(CustomException e) {
		return ErrorResponse.toResponseEntity(e.getStatusCode().getCode(), e.getStatusCode().getMessage());
	}
}
