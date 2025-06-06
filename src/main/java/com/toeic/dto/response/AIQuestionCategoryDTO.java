package com.toeic.dto.response;

import lombok.Data;

@Data
public class AIQuestionCategoryDTO {
    private String categoryName;
    private int correctAnswers;
    private int totalQuestions;
    private double accuracy;
    private String feedback;
} 