package com.toeic.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.toeic.service.AnalyticService;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;

import com.toeic.dto.response.AdminDashboardStats;
import com.toeic.dto.response.ApiResponse;

@RestController
@RequestMapping("/api/v1/admin/analytic")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AdminAnalyticController {

    private final AnalyticService analyticService;

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<AdminDashboardStats>> getAdminDashboardStats() {
        AdminDashboardStats stats = analyticService.getAdminDashboardStats();
        ApiResponse<AdminDashboardStats> response = ApiResponse.success(HttpStatus.OK, "Admin dashboard stats fetched successfully", stats);
        return ResponseEntity.ok(response);
    }
        
}
