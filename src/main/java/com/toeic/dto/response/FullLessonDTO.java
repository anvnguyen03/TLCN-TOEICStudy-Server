package com.toeic.dto.response;

import java.util.List;

import lombok.Data;

@Data
// return lesson detail for admin course list usecase
public class FullLessonDTO {
    private Long id;
    private String title;
    private String description;
    private String type;
    private int duration;
    private int orderIndex;
    private Boolean isFree;
    private String content;
    private String videoUrl;
    private List<FullQuizQuestionDTO> quizQuestions;
}