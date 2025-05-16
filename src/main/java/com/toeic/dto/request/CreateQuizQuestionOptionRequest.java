package com.toeic.dto.request;

import lombok.Data;

@Data
public class CreateQuizQuestionOptionRequest {
    private String optionText1;
    private String optionText2;
    private String optionText3;
    private String correctOption;
} 