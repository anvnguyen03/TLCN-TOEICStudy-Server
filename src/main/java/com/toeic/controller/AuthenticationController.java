package com.toeic.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.toeic.dto.request.ActivateRequest;
import com.toeic.dto.request.LoginRequest;
import com.toeic.dto.request.RegisterRequest;
import com.toeic.dto.response.ApiResponse;
import com.toeic.dto.response.LoginResponse;
import com.toeic.entity.User;
import com.toeic.service.AuthenticationService;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthenticationController {

	private final AuthenticationService authenticationService;
	
	@PostMapping("/register")
	public ResponseEntity<ApiResponse<User>> register(@RequestBody RegisterRequest registerRequest) throws MessagingException {
		User createdUser = authenticationService.register(registerRequest);
		ApiResponse<User> response = ApiResponse.success(
				HttpStatus.CREATED, 
				"Register successfully. Please check your email to get activation link.", 
				createdUser);
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}
	
	@PostMapping("/register/verify")
	public ResponseEntity<ApiResponse<String>> activateAccount(@RequestBody ActivateRequest activateRequest) {
		authenticationService.activateAccount(activateRequest);
		ApiResponse<String> response = ApiResponse.success(HttpStatus.OK, "Your account has been activated successfully.", null);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@PostMapping("/login")
	public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody LoginRequest loginRequest) {
		LoginResponse loginResponse = authenticationService.login(loginRequest);
		ApiResponse<LoginResponse> response = ApiResponse.success(HttpStatus.OK, "Logged in", loginResponse);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
}
