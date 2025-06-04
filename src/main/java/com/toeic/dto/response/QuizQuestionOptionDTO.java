package com.toeic.dto.response;

import lombok.Data;

@Data
public class QuizQuestionOptionDTO {
    private Long id;
    private int orderIndex;
    private String optionText1;
    private String optionText2;
    private String optionText3;
}
