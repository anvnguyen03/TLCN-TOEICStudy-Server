package com.toeic.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.toeic.dto.response.ApiResponse;
import com.toeic.dto.response.ResultHistoryByTest;
import com.toeic.entity.User;
import com.toeic.service.AccountService;
import com.toeic.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UserController {
	
	private final UserService userService;
	private final AccountService accountService;

	@GetMapping("/test-history")
	public ResponseEntity<ApiResponse<List<ResultHistoryByTest>>> getTestHistory(HttpServletRequest request) {
		String authorizationHeader = request.getHeader("Authorization");
		String token = authorizationHeader.substring(7);
		User user = accountService.fetchAccount(token);
		
		List<ResultHistoryByTest> testHistory = userService.getTestHistory(user);
		ApiResponse<List<ResultHistoryByTest>> response = ApiResponse.success(
				HttpStatus.OK, "Get test history successfully", testHistory);
		return ResponseEntity.ok(response);
	}
}
