package com.toeic.controller;

import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.toeic.dto.request.AdminUpdateUserRequest;
import com.toeic.dto.response.ApiResponse;
import com.toeic.dto.response.UserDTO;
import com.toeic.dto.response.UserDetailDTO;
import com.toeic.entity.ERole;
import com.toeic.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AdminUserController {

	private final UserService userService;
	
	@GetMapping("/user/all")
	public ResponseEntity<ApiResponse<Page<UserDTO>>> getAllUsers(
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "10") int size,
		@RequestParam(defaultValue = "id") String sortBy
	) {
		Page<UserDTO> users = userService.getAllUsersWithPagination(page, size, sortBy);
		ApiResponse<Page<UserDTO>> response = ApiResponse.success(
			HttpStatus.OK, 
			"Get all users successfully", 
			users
		);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/user/search")
	public ResponseEntity<ApiResponse<List<UserDTO>>> searchUsers(
		@RequestParam(required = false) String keyword,
		@RequestParam(required = false) ERole role,
		@RequestParam(required = false, defaultValue = "ALL") String isActivated
	) {
		Boolean activated = null;
		if (!"ALL".equals(isActivated)) {
			activated = Boolean.parseBoolean(isActivated);
		}
		List<UserDTO> users = userService.searchUsers(keyword, role, activated);
		ApiResponse<List<UserDTO>> response = ApiResponse.success(
			HttpStatus.OK, 
			"Search users successfully", 
			users
		);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/user/{userId}")
	public ResponseEntity<ApiResponse<UserDetailDTO>> getUserDetail(
		@PathVariable long userId
	) {
		UserDetailDTO userDetail = userService.getUserDetail(userId);
		ApiResponse<UserDetailDTO> response = ApiResponse.success(
			HttpStatus.OK, 
			"Get user detail successfully", 
			userDetail
		);
		return ResponseEntity.ok(response);
	}

	@PutMapping("/user/update")
	public ResponseEntity<ApiResponse<UserDTO>> updateUser(@RequestBody AdminUpdateUserRequest request) {
		UserDTO updatedUser = userService.updateUser(request);
		ApiResponse<UserDTO> response = ApiResponse.success(
				HttpStatus.OK, "User updated successfully", updatedUser);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@PutMapping("/user/toggle-status")
	public ResponseEntity<ApiResponse<UserDTO>> toggleUserStatus(
		@RequestParam long userId,
		@RequestParam boolean isActivated
	) {
		UserDTO updatedUser = userService.toggleUserStatus(userId, isActivated);
		ApiResponse<UserDTO> response = ApiResponse.success(
			HttpStatus.OK, 
			"User status updated successfully", 
			updatedUser
		);
		return ResponseEntity.ok(response);
	}
	
	@DeleteMapping("/user/delete")
	public ResponseEntity<ApiResponse<String>> deleteUser(@RequestParam long userId) {
		userService.deleteUser(userId);
		ApiResponse<String> response = ApiResponse.success(
				HttpStatus.OK, "User deleted successfully", "User with ID " + userId + " has been deleted");
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@GetMapping("/user/export")
	public ResponseEntity<Resource> exportUsers(
		@RequestParam(defaultValue = "xlsx") String format
	) {
		Resource file = userService.exportUsers(format);
		return ResponseEntity.ok()
			.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"users." + format + "\"")
			.contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
			.body(file);
	}
}
