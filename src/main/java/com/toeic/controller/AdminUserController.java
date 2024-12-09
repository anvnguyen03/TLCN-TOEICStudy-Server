package com.toeic.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.toeic.dto.response.ApiResponse;
import com.toeic.dto.response.UserDTO;
import com.toeic.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AdminUserController {

	private final UserService userService;
	
	@GetMapping("/user/all")
	public ResponseEntity<ApiResponse<List<UserDTO>>> getAllUsers() {
		List<UserDTO> users = userService.getAllUsers();
		ApiResponse<List<UserDTO>> response = ApiResponse.success(HttpStatus.OK, "Get all user successfully", users);
		return ResponseEntity.ok(response);
	}
}
