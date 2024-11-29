package com.toeic.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.toeic.dto.response.ApiResponse;
import com.toeic.service.TestService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AdminController {

	private final TestService testService;
	
	@PostMapping("/test/upload")
	public ResponseEntity<ApiResponse<String>> uploadTest(
			@RequestParam("file") MultipartFile file,
			@RequestParam("images") List<MultipartFile> images,
			@RequestParam("audios") List<MultipartFile> audios,
			@RequestParam("categoryName") String categoryName) {
		testService.uploadTest(file, images, audios, categoryName);
		ApiResponse<String> response = ApiResponse.success(
				HttpStatus.CREATED, 
				"Files imported successfully. New test have been created!",
				null);
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}
}
