package com.toeic.service;

import com.toeic.dto.response.AdminDashboardStats;
import com.toeic.dto.response.TestAnalyticsDTO;

public interface AnalyticService {
    TestAnalyticsDTO getUserTestAnalytics(Long userId);
    AdminDashboardStats getAdminDashboardStats();
}
