package com.toeic.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.toeic.dto.response.ApiResponse;
import com.toeic.entity.User;
import com.toeic.service.AccountService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/account")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AccountController {

	private final AccountService accountService;
	
	@GetMapping("/fetch-account")
	public ResponseEntity<ApiResponse<User>> fetchAccount(HttpServletRequest request) {
		String authorizationHeader = request.getHeader("Authorization");
		String token = authorizationHeader.substring(7);	// Bearer <token>
		User user = accountService.fetchAccount(token);
		ApiResponse<User> response = ApiResponse.success(
				HttpStatus.OK, 
				"Fetch successfully.", 
				user);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
}
