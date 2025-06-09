package com.toeic.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.toeic.dto.response.ApiResponse;
import com.toeic.dto.response.UserLearningDTO;
import com.toeic.entity.User;
import com.toeic.service.AccountService;
import com.toeic.service.CourseEnrollmentService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/course-enrollment")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CourseEnrollmentController {

    private final CourseEnrollmentService courseEnrollmentService;
    private final AccountService accountService;
    // Get user's course enrolled by user id
    @GetMapping("/user-learning")
    public ResponseEntity<ApiResponse<List<UserLearningDTO>>> getUserLearningInfo(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");
		String token = authorizationHeader.substring(7);
		User user = accountService.fetchAccount(token);
        List<UserLearningDTO> userLearningInfo = courseEnrollmentService.getUserCoursesEnrolled(user.getId());
        ApiResponse<List<UserLearningDTO>> response = ApiResponse.success(
                HttpStatus.OK, "Get user learning info successfully", userLearningInfo);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


}
