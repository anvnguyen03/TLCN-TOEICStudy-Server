package com.toeic.dto.response;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
	private HttpStatus status;
	private String message;
	private T data;
	private String error;
	
	public static <T> ApiResponse<T> success(HttpStatus status, String message, T data) {
		return new ApiResponse<>(status, message, data, null);
	}
	
	public static <T> ApiResponse<T> error(HttpStatus status, String message, String error) {
		return new ApiResponse<>(status, message, null, error);
	}
}
