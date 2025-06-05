package com.toeic.dto.response;

import java.time.LocalDate;
import java.util.List;
import lombok.Data;

@Data
public class TestAnalyticsDTO {
    private int currentScore;
    private int previousScore;
    private int maxPossibleScore;
    private int testsTaken;
    private String strongestPart;
    private String weakestPart;
    
    private ImprovementStatsDTO improvementStats;
    private OverallStatsDTO overallStats;
    private List<ScoreHistoryDTO> scoreHistory;
    private List<PartPerformanceDTO> partPerformance;
    private List<recentTestsDTO> recentTests;
    
    @Data
    public static class ImprovementStatsDTO {
        private int totalImprovement;
        private String improvementTrend;
    }
    
    @Data
    public static class OverallStatsDTO {
        private int totalTestsCompleted;
        private int totalQuestionsAnswered;
        private double averageAccuracy;
        private int totalStudyTime;
        private double listeningAccuracy;
        private double readingAccuracy;
        private int bestScore;
        private int averageScore;
    }
    
    @Data
    public static class ScoreHistoryDTO {
        private String date;
        private int listening;
        private int reading;
        private int total;
    }
    
    @Data
    public static class PartPerformanceDTO {
        private int part;
        private String name;
        private double userAccuracy;
        private double avgAccuracy;
    }

    @Data
    public static class recentTestsDTO {
        private Long id;
        private String title;
        private LocalDate date;
        private int listening;
        private int reading;
        private int total;
        private int timeToComplete; // in seconds
    }
} 