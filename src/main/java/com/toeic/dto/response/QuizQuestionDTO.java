package com.toeic.dto.response;

import lombok.Data;

@Data
public class QuizQuestionDTO {
    private Long id;
    private String type;
    private int orderIndex;
    private String question;
    private QuizQuestionOptionDTO option;
    private CardMatchingQuestionDTO pairs;
}
