package com.toeic.dto.response;

import lombok.Data;

@Data
public class AIPartAnalysisDTO {
    private int partNumber;
    private String partName;
    private int correctAnswers;
    private int totalQuestions;
    private double accuracy;
    private String feedback;
} 