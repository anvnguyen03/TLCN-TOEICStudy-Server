package com.toeic.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.toeic.dto.response.ApiResponse;
import com.toeic.dto.response.TestInfoDTO;
import com.toeic.entity.Test;
import com.toeic.service.TestService;
import com.toeic.utils.DTOMapperUtils;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AdminTestController {

	private final TestService testService;
	
	@GetMapping("/test/all")
	public ResponseEntity<ApiResponse<List<TestInfoDTO>>> getAllTestInfo() {
		List<TestInfoDTO> testsInfo = testService.getAllTestInfo();
		ApiResponse<List<TestInfoDTO>> response = ApiResponse.success(
				HttpStatus.OK, "Get all test info sucessfull", testsInfo);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@DeleteMapping("/test/delete")
	public ResponseEntity<ApiResponse<String>> deleteTest(
			@RequestParam(required = true) long testId) {
		testService.deleteTest(testId);
		ApiResponse<String> response = ApiResponse.success(
				HttpStatus.OK, "Delete Test Successfully", null);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@PostMapping("/test/upload/full-test")
	public ResponseEntity<ApiResponse<Long>> uploadFullTest(
			@RequestParam("file") MultipartFile file,
			@RequestParam("images") List<MultipartFile> images,
			@RequestParam("audios") List<MultipartFile> audios) {
		Test newTest = testService.uploadFullTest(file, images, audios);
		ApiResponse<Long> response = ApiResponse.success(
				HttpStatus.CREATED, 
				"Files imported successfully. New test have been created!",
				newTest.getId());
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}
}
