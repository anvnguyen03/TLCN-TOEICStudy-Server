package com.toeic.dto.request;

import lombok.Data;

@Data
public class MultipleChoiceAnswerRequest {
    private Long quizQuestionId;
    private String selectedOption;
} 