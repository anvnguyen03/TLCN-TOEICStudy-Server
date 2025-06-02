package com.toeic.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.toeic.dto.response.ApiResponse;
import com.toeic.dto.response.CourseCardDTO;
import com.toeic.dto.response.CourseInfoDTO;
import com.toeic.dto.response.CourseReviewDTO;
import com.toeic.service.CourseService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/course")
@CrossOrigin(origins = "*")
public class CourseController {
    
    private final CourseService courseService;

    @GetMapping("/all-published")
    public ResponseEntity<ApiResponse<List<CourseCardDTO>>> getAllPublishedCourses() {
        List<CourseCardDTO> courses = courseService.getPublishedCourses();
        ApiResponse<List<CourseCardDTO>> response = ApiResponse.success(HttpStatus.OK, "Get all courses successfully", courses);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{courseId}")
    public ResponseEntity<ApiResponse<CourseInfoDTO>> getCourseById(@PathVariable Long courseId) {
        CourseInfoDTO course = courseService.getCourseById(courseId);
        ApiResponse<CourseInfoDTO> response = ApiResponse.success(HttpStatus.OK, "Get course successfully", course);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{courseId}/recent-reviews")
    public ResponseEntity<ApiResponse<List<CourseReviewDTO>>> getCourseRecentReviews(@PathVariable Long courseId) {
        List<CourseReviewDTO> reviews = courseService.getCourseRecentReviews(courseId);
        ApiResponse<List<CourseReviewDTO>> response = ApiResponse.success(HttpStatus.OK, "Get course recent reviews successfully", reviews);
        return ResponseEntity.ok(response);
    }

}
