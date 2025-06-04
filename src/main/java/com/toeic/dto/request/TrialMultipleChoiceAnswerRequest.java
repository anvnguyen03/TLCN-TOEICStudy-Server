package com.toeic.dto.request;

import lombok.Data;

@Data
public class TrialMultipleChoiceAnswerRequest {
    private Long quizQuestionId;
    private String selectedOption;
} 