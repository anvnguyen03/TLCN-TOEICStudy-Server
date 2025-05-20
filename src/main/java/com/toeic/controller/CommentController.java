package com.toeic.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.toeic.dto.request.CommentRequest;
import com.toeic.dto.response.ApiResponse;
import com.toeic.dto.response.CommentDTO;
import com.toeic.service.CommentService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/comment")
@CrossOrigin(origins = "*")
public class CommentController {

	private final CommentService commentService;
	
	@GetMapping("/test/{testId}")
	public ResponseEntity<ApiResponse<List<CommentDTO>>> getCommentsByTest(@PathVariable long testId) {
		List<CommentDTO> comments = commentService.getCommentsByTest(testId);
		ApiResponse<List<CommentDTO>> response = ApiResponse.success(
				HttpStatus.OK, "Get comments successfully", comments);
		return ResponseEntity.ok(response);
	}
	
	@PostMapping("/add")
	public ResponseEntity<ApiResponse<CommentDTO>> addComment(@RequestBody CommentRequest commentRequest) {
		CommentDTO comment = commentService.addComment(commentRequest);
		ApiResponse<CommentDTO> response = ApiResponse.success(
				HttpStatus.OK, "New comment has been added", comment);
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}
	
	@DeleteMapping("/delete/{commentId}")
	public ResponseEntity<ApiResponse<String>> deleteComment(@PathVariable long commentId) {
		commentService.deleteComment(commentId);
		ApiResponse<String> response = ApiResponse.success(
				HttpStatus.OK, "Comment deleted", null);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
}
