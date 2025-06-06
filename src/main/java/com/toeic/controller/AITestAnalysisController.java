package com.toeic.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.toeic.dto.response.ApiResponse;
import com.toeic.dto.response.AITestAnalysisDTO;
import com.toeic.dto.response.TestAnalyticsDTO;
import com.toeic.entity.User;
import com.toeic.entity.AITestAnalysis;
import com.toeic.service.AITestAnalysisService;
import com.toeic.service.AccountService;
import com.toeic.exception.AnalysisLimitExceededException;
import com.toeic.exception.ResourceNotFoundException;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RestController
@RequestMapping("/api/v1/ai-analysis")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AITestAnalysisController {

    private final AITestAnalysisService aiTestAnalysisService;
    private final AccountService accountService;

    @PostMapping("/test")
    public ResponseEntity<ApiResponse<AITestAnalysisDTO>> getAITestAnalysis(
            @RequestBody TestAnalyticsDTO analytics,
            HttpServletRequest request) {
        try {
            String authorizationHeader = request.getHeader("Authorization");
            String token = authorizationHeader.substring(7);
            User user = accountService.fetchAccount(token);
            
            // Generate AI analysis using the provided analytics
            AITestAnalysisDTO analysis = aiTestAnalysisService.analyzeTestResult(user.getId(), analytics);
            
            ApiResponse<AITestAnalysisDTO> response = ApiResponse.success(
                HttpStatus.OK, 
                "AI test analysis completed successfully", 
                analysis
            );
            return ResponseEntity.ok(response);
        } catch (AnalysisLimitExceededException e) {
            ApiResponse<AITestAnalysisDTO> response = ApiResponse.error(
                HttpStatus.TOO_MANY_REQUESTS,
                e.getMessage(),
                null
            );
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(response);
        }
    }

    @GetMapping("/latest")
    public ResponseEntity<ApiResponse<AITestAnalysisDTO>> getLatestAnalysis(HttpServletRequest request) {
        try {
            String authorizationHeader = request.getHeader("Authorization");
            String token = authorizationHeader.substring(7);
            User user = accountService.fetchAccount(token);
            
            AITestAnalysisDTO analysis = aiTestAnalysisService.getLatestAnalysis(user.getId());
            
            ApiResponse<AITestAnalysisDTO> response = ApiResponse.success(
                HttpStatus.OK,
                "Latest AI test analysis retrieved successfully",
                analysis
            );
            return ResponseEntity.ok(response);
        } catch (ResourceNotFoundException e) {
            ApiResponse<AITestAnalysisDTO> response = ApiResponse.error(
                HttpStatus.NOT_FOUND,
                e.getMessage(),
                null
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
} 