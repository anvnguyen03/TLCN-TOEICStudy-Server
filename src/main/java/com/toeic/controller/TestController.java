package com.toeic.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.toeic.dto.response.ApiResponse;
import com.toeic.dto.response.TestInfoDTO;
import com.toeic.dto.response.UserResultDTO;
import com.toeic.entity.User;
import com.toeic.service.AccountService;
import com.toeic.service.TestService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/test")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TestController {

	private final AccountService accountService;
	private final TestService testService;
	
	@GetMapping("/{testId}/info")
	public ResponseEntity<ApiResponse<TestInfoDTO>> getTestInfo(@PathVariable long testId) {
		TestInfoDTO testInfo = testService.getTestInfo(testId);
		ApiResponse<TestInfoDTO> response = ApiResponse.success(
				HttpStatus.OK, "Get test info sucessfull", testInfo);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@GetMapping("/all/info")
	public ResponseEntity<ApiResponse<List<TestInfoDTO>>> getAllTestInfo() {
		List<TestInfoDTO> testsInfo = testService.getAllTestInfo();
		ApiResponse<List<TestInfoDTO>> response = ApiResponse.success(
				HttpStatus.OK, "Get all test info sucessfull", testsInfo);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	@GetMapping("/all-published")
	public ResponseEntity<Page<TestInfoDTO>> searchTests(
			@RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) {
		
		String authorizationHeader = request.getHeader("Authorization");
		Page<TestInfoDTO> testInfoDTOPage = null;
		// Case: User chưa đăng nhập, fetch test không xem được trạng thái isUserAttemped của Test
		if ((authorizationHeader == null) || (authorizationHeader.trim().isEmpty())) {
			testInfoDTOPage = testService.getByPublishedStatusAndKeywordWithPagination(keyword, page, size, null);
		} else {
			// Case: User đã đăng nhập, fetch test xem được trạng thái isUserAttemped của Test
			String token = authorizationHeader.substring(7);
			User user = accountService.fetchAccount(token);
			
			testInfoDTOPage = testService.getByPublishedStatusAndKeywordWithPagination(keyword, page, size, user);
		}
		
		return ResponseEntity.ok(testInfoDTOPage);
	}
	
	@GetMapping("/results/{resultId}")
	public ResponseEntity<ApiResponse<UserResultDTO>> getUserResult(@PathVariable long resultId, HttpServletRequest request) {
		String authorizationHeader = request.getHeader("Authorization");
		String token = authorizationHeader.substring(7);	// Bearer <token>
		User user = accountService.fetchAccount(token);
		
		UserResultDTO userResult = testService.getUserResult(user, resultId);
		ApiResponse<UserResultDTO> response = ApiResponse.success(
				HttpStatus.OK, "Get user result successfull", userResult);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
}
