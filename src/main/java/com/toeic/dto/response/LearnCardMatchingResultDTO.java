package com.toeic.dto.response;

import java.util.List;

import lombok.Data;

@Data
public class LearnCardMatchingResultDTO {
    private boolean isCompleted;
    private Long lessonId;
    private int totalPairs;
    private int correctPairs;
    private List<CardMatchingPairResult> results;
    
    @Data
    public static class CardMatchingPairResult {
        private Long promptId;
        private String selectedAnswer;
        private String correctAnswer;
        private boolean isCorrect;
    }
}
