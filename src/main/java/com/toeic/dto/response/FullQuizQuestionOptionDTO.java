package com.toeic.dto.response;

import lombok.Data;

@Data
// return quiz question option detail for admin course list usecase
public class FullQuizQuestionOptionDTO {
    private Long id;
    private String optionText1;
    private String optionText2;
    private String optionText3;
    private String correctOption;
}
