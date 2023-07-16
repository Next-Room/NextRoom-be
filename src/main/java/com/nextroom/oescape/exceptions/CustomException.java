package com.nextroom.oescape.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CustomException extends RuntimeException {
	private final StatusCode statusCode;
}
