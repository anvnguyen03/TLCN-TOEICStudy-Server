package com.toeic.dto.response;

import java.util.List;
import lombok.Data;

@Data
public class TrialMultipleChoiceResultDTO {
    private int totalQuestions;
    private int correctAnswers;
    private List<QuizAnswerResultDTO> results;
} 