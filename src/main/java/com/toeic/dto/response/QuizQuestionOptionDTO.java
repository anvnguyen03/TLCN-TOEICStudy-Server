package com.toeic.dto.response;

import lombok.Data;

@Data
// return quiz question option detail for user learn course usecase
public class QuizQuestionOptionDTO {
    private Long id;
    private String optionText1;
    private String optionText2;
    private String optionText3;
}
