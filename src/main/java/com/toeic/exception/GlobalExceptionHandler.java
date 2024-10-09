package com.toeic.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.toeic.dto.response.ApiResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {
	
	@ExceptionHandler(UserAlreadyExistsException.class)
	public ResponseEntity<ApiResponse<String>> handleUserAlreadyExists(UserAlreadyExistsException e) {
		ApiResponse<String> response = ApiResponse.error(HttpStatus.CONFLICT, e.getMessage(), "User already exists");
		return new ResponseEntity<>(response, HttpStatus.CONFLICT);
	}
	
	@ExceptionHandler(InvalidEmailException.class)
    public ResponseEntity<ApiResponse<String>> handleInvalidEmail(InvalidEmailException ex) {
        ApiResponse<String> response = ApiResponse.error(HttpStatus.BAD_REQUEST, ex.getMessage(), "Invalid email address");
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
	
	@ExceptionHandler(InvalidPasswordException.class)
    public ResponseEntity<ApiResponse<String>> handleInvalidPassword(InvalidPasswordException ex) {
        ApiResponse<String> response = ApiResponse.error(HttpStatus.BAD_REQUEST, ex.getMessage(), "Invalid password");
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
	
	// handle all exceptions that not defined
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<String>> handleGenericException(Exception ex) {
        ApiResponse<String> response = ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong", ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
