package com.toeic.service;

import com.toeic.dto.response.AITestAnalysisDTO;
import com.toeic.dto.response.TestAnalyticsDTO;

public interface AITestAnalysisService {
    AITestAnalysisDTO analyzeTestResult(Long userId, TestAnalyticsDTO analytics);
    AITestAnalysisDTO getLatestAnalysis(Long userId);
} 