package com.toeic.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.toeic.dto.request.CreateCourseRequest;
import com.toeic.dto.response.ApiResponse;
import com.toeic.dto.response.CourseInfoDTO;
import com.toeic.dto.response.FullCourseDTO;
import com.toeic.service.CourseService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/admin/course")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AdminCourseController {

    private final CourseService courseService;

    @PostMapping(value = "/create", consumes = "multipart/form-data")
    public ResponseEntity<ApiResponse<CourseInfoDTO>> createCourse(CreateCourseRequest request) throws IOException {
        CourseInfoDTO course = courseService.createCourse(request);
        ApiResponse<CourseInfoDTO> response = ApiResponse.success(HttpStatus.OK, "Course created successfully", course);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<FullCourseDTO>>> getAllCourses() {
        List<FullCourseDTO> courses = courseService.getAllCoursesDetailForAdmin();
        ApiResponse<List<FullCourseDTO>> response = ApiResponse.success(HttpStatus.OK, "Get all courses successfully", courses);
        return ResponseEntity.ok(response);
    }
}
