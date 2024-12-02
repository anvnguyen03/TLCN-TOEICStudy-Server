package com.toeic.controller;

import java.util.List;

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
import com.toeic.dto.response.TestInfoPagingDTO;
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
	
	@GetMapping("/all")
	public ResponseEntity<ApiResponse<List<TestInfoDTO>>> getAllTestInfo() {
		List<TestInfoDTO> testsInfo = testService.getAllTestInfo();
		ApiResponse<List<TestInfoDTO>> response = ApiResponse.success(
				HttpStatus.OK, "Get all test info sucessfull", testsInfo);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@GetMapping("/all-published")
	public ResponseEntity<ApiResponse<TestInfoPagingDTO>> searchTests(
			@RequestParam(required = false) String keyword,
			@RequestParam(required = false) Long testCategoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) {
		
		String authorizationHeader = request.getHeader("Authorization");
		TestInfoPagingDTO testInfoDTOPaging = null;
		// Case: User chưa đăng nhập, fetch test không xem được trạng thái isUserAttemped của Test
		if ((authorizationHeader == null) || (authorizationHeader.trim().isEmpty())) {
			testInfoDTOPaging = testService.getByPublishedStatusAndKeywordWithPagination(keyword, testCategoryId, page, size, null);
		} else {
			// Case: User đã đăng nhập, fetch test xem được trạng thái isUserAttemped của Test
			String token = authorizationHeader.substring(7);
			User user = accountService.fetchAccount(token);
			
			testInfoDTOPaging = testService.getByPublishedStatusAndKeywordWithPagination(keyword, testCategoryId, page, size, user);
		}
		
		ApiResponse<TestInfoPagingDTO> response = ApiResponse.success(
				HttpStatus.OK, "Fetch tests with paginator successfull", testInfoDTOPaging);
		
		return ResponseEntity.ok(response);
	}
	
	@GetMapping("/{testId}/info")
	public ResponseEntity<ApiResponse<TestInfoDTO>> getTestInfo(
			@PathVariable long testId,
			HttpServletRequest request) {
		TestInfoDTO testInfo = null;
		String authorizationHeader = request.getHeader("Authorization");
		if ((authorizationHeader == null) || (authorizationHeader.trim().isEmpty())) {
			testInfo = testService.getTestInfo(testId);
		} else {
			String token = authorizationHeader.substring(7);
			User user = accountService.fetchAccount(token);
			testInfo = testService.getTestInfoForLoggedInUser(user, testId);
		}
		
		ApiResponse<TestInfoDTO> response = ApiResponse.success(
				HttpStatus.OK, "Get test info sucessfull", testInfo);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@GetMapping("/{testId}/results")
	public ResponseEntity<ApiResponse<List<UserResultDTO>>> getUserResultsForUser(
			@PathVariable long testId,
			HttpServletRequest request) {
		String authorizationHeader = request.getHeader("Authorization");
		String token = authorizationHeader.substring(7);
		User user = accountService.fetchAccount(token);
		
		List<UserResultDTO> userResults = testService.getUserResultsForUser(user, testId);
		ApiResponse<List<UserResultDTO>> response = ApiResponse.success(
				HttpStatus.OK, "Get user results successfull", userResults);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@GetMapping("/results/{resultId}")
	public ResponseEntity<ApiResponse<UserResultDTO>> getOneUserResult(@PathVariable long resultId, HttpServletRequest request) {
		String authorizationHeader = request.getHeader("Authorization");
		String token = authorizationHeader.substring(7);
		User user = accountService.fetchAccount(token);
		
		UserResultDTO userResult = testService.getUserResult(user, resultId);
		ApiResponse<UserResultDTO> response = ApiResponse.success(
				HttpStatus.OK, "Get user result successfull", userResult);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
}
