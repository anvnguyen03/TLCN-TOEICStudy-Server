package com.toeic.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.toeic.dto.request.SubmitFullTestRequest;
import com.toeic.dto.response.ApiResponse;
import com.toeic.dto.response.DisplayTestItemDTO;
import com.toeic.dto.response.UserResultDTO;
import com.toeic.service.ScoringService;
import com.toeic.service.TestService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/do-test")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DoTestController {

	private final TestService testService;
	private final ScoringService scoringService;
	
	@GetMapping("/{testId}/get-test-items")
	public ResponseEntity<ApiResponse<List<DisplayTestItemDTO>>> getDisplayTestItems(@PathVariable long testId) {
		List<DisplayTestItemDTO> displayItems = testService.getDisplayTestItem(testId);
		ApiResponse<List<DisplayTestItemDTO>> response = ApiResponse.success(
				HttpStatus.OK, "Get test items successfully", displayItems);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@PostMapping("/submit")
	public ResponseEntity<ApiResponse<UserResultDTO>> submitFullTest(@RequestBody SubmitFullTestRequest submitRequest) {
		UserResultDTO userResult = scoringService.submitFullTest(submitRequest);
		ApiResponse<UserResultDTO> response = ApiResponse.success(
				HttpStatus.OK, "Submit full test successfully", userResult);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
}
