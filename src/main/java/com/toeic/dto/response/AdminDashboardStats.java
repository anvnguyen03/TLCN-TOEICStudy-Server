package com.toeic.dto.response;

import java.util.List;

import lombok.Data;

@Data
public class AdminDashboardStats {
    // Key metrics
    private int totalUsers;
    private int lastMonthUsers;
    private int totalRevenue;
    private int monthlyRevenue;
    private int lastMonthRevenue;
    private int testAttemps;
    private int lastMonthTestAttemps;
    private int courseEnrollments;
    private int lastMonthCourseEnrollments;


    // Top courses by rating
    private List<TopCourse> topCourses;

    private RevenueTrend revenueTrend;
    private UserGrowth userGrowth;
    private TestScoreDistribution testScoreDistribution;

    @Data
    public class TopCourse {
        private int id;
        private String title;
        private int enrollments;
        private int revenue;
        private double rating;
    }

    @Data
    // last 6 months revenue trend
    public static class RevenueTrend {
        private List<String> labels;
        private List<Integer> data;
    }

    @Data
    // last 6 months user growth
    public static class UserGrowth {
        private List<String> labels;
        private List<Integer> newUsers;
        private List<Integer> activeUsers;
    }

    @Data
    // Average test score distribution by score range
    public static class TestScoreDistribution {
        private List<String> labels;
        private List<Integer> data;
    }

}
