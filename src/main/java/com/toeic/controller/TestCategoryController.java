package com.toeic.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.toeic.dto.response.ApiResponse;
import com.toeic.dto.response.TestCategoryDTO;
import com.toeic.service.TestCategoryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/test-category")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TestCategoryController {
	
	private final TestCategoryService testCategoryService;

	@GetMapping("/all")
	public ResponseEntity<ApiResponse<List<TestCategoryDTO>>> getAll() {
		List<TestCategoryDTO> testCategories = testCategoryService.getAllTestCategory();
		ApiResponse<List<TestCategoryDTO>> response = ApiResponse.success
				(HttpStatus.OK, "Fetch all categories successfull", testCategories);
		return ResponseEntity.ok(response);
	}
}
