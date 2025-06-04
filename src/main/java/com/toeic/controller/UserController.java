package com.toeic.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
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
	private final Cloudinary cloudinary;

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
	
	@PostMapping("/update")
	public ResponseEntity<ApiResponse<String>> updateInfo(
			HttpServletRequest request, 
			@RequestParam(required = true) String fullname) {
		String authorizationHeader = request.getHeader("Authorization");
		String token = authorizationHeader.substring(7);
		User user = accountService.fetchAccount(token);
		
		user.setFullname(fullname);
		userService.update(user);
		
		ApiResponse<String> response = ApiResponse.success(
				HttpStatus.OK, "Info updated", fullname);
		return ResponseEntity.ok(response);
	}

	@PostMapping("/avatar")
	public ResponseEntity<ApiResponse<String>> uploadAvatar(
			HttpServletRequest request,
			@RequestParam("avatar") MultipartFile file) {
		try {
			String authorizationHeader = request.getHeader("Authorization");
			String token = authorizationHeader.substring(7);
			User user = accountService.fetchAccount(token);

			// Upload to Cloudinary
			Map params = ObjectUtils.asMap(
				"folder", "TOEIC-Study/avatars",
				"public_id", user.getId() + "_" + System.currentTimeMillis()
			);
			
			Map uploadResult = cloudinary.uploader().upload(file.getBytes(), params);
			String avatarUrl = (String) uploadResult.get("secure_url");
			
			// Update user avatar
			user.setAvatar(avatarUrl);
			userService.update(user);
			
			return ResponseEntity.ok(ApiResponse.success(
				HttpStatus.OK,
				"Avatar uploaded successfully",
				avatarUrl
			));
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(ApiResponse.error(
				HttpStatus.BAD_REQUEST,
				"Failed to upload avatar: " + e.getMessage(),
				null
			));
		}
	}
}
