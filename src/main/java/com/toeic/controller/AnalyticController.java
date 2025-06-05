package com.toeic.controller;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.toeic.service.AccountService;
import com.toeic.service.AnalyticService;

import jakarta.servlet.http.HttpServletRequest;

import com.toeic.dto.response.ApiResponse;
import com.toeic.dto.response.TestAnalyticsDTO;
import com.toeic.entity.User;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/analytic")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AnalyticController {

    private final AnalyticService analyticService;
    private final AccountService accountService;
    @GetMapping("/test")
    public ResponseEntity<ApiResponse<TestAnalyticsDTO>> getUserTestAnalytics(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");
        String token = authorizationHeader.substring(7);
        User user = accountService.fetchAccount(token);
        
        TestAnalyticsDTO analytics = analyticService.getUserTestAnalytics(user.getId());
        ApiResponse<TestAnalyticsDTO> response = ApiResponse.success(HttpStatus.OK, "User test analytics fetched successfully", analytics);
        return ResponseEntity.ok(response);
    }
}
