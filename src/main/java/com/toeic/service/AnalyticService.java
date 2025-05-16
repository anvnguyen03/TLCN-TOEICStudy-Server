package com.toeic.service;

import com.toeic.dto.TestAnalyticsDTO;

public interface AnalyticService {
    TestAnalyticsDTO getUserTestAnalytics(Long userId);
}
