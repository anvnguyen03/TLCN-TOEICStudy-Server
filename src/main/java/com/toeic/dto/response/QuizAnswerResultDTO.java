package com.toeic.dto.response;

import lombok.Data;

@Data
public class QuizAnswerResultDTO {
    private Long quizQuestionId;
    private boolean isCorrect;
    private String correctOption;
    private String selectedOption;
} 