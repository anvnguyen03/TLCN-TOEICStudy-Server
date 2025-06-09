package com.toeic.controller;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.toeic.dto.request.CourseReviewRequest;
import com.toeic.dto.response.ApiResponse;
import com.toeic.dto.response.CourseReviewPagingDTO;
import com.toeic.dto.response.CourseReviewStatistics;
import com.toeic.entity.User;
import com.toeic.service.AccountService;
import com.toeic.service.CourseReviewService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/course-review")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CourseReviewController {

    private final CourseReviewService courseReviewService;
    private final AccountService accountService;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<String>> createCourseReview(@RequestBody CourseReviewRequest courseReview, HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");
		String token = authorizationHeader.substring(7);
		User user = accountService.fetchAccount(token);

        courseReviewService.createCourseReview(courseReview, user);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "Course review created successfully", null));
    }

    @PutMapping("/update")
    public ResponseEntity<ApiResponse<String>> updateCourseReview(@RequestBody CourseReviewRequest courseReview, HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");
		String token = authorizationHeader.substring(7);
		User user = accountService.fetchAccount(token);
        courseReviewService.updateCourseReview(courseReview, user);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "Course review updated successfully", null));
    }

    @GetMapping("/get-statistics/{courseId}")
    public ResponseEntity<ApiResponse<CourseReviewStatistics>> getCourseReviewStatistics(@PathVariable Long courseId) {
        CourseReviewStatistics courseReviewStatistics = courseReviewService.getCourseReviewStatistics(courseId);
        ApiResponse<CourseReviewStatistics> response = ApiResponse.success(HttpStatus.OK, "Course review statistics fetched successfully", courseReviewStatistics);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/get-reviews-pagination/{courseId}")
    public ResponseEntity<ApiResponse<CourseReviewPagingDTO>> getCourseReviewsPagination(
            @PathVariable Long courseId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int size) {
        CourseReviewPagingDTO courseReviews = courseReviewService.getCourseReviewsPagination(courseId, PageRequest.of(page, size));
        ApiResponse<CourseReviewPagingDTO> response = ApiResponse.success(HttpStatus.OK, "Course reviews fetched successfully", courseReviews);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
