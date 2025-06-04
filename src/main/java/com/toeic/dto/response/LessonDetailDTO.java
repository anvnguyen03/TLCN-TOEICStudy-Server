package com.toeic.dto.response;

import java.util.List;

import lombok.Data;

@Data
public class LessonDetailDTO {
    private Long id;
    private String title;
    private String description;
    private String type;
    private int duration;
    private int orderIndex;
    private Boolean isFree;

    // For TEXT lesssons
    private String content;

    // For VIDEO lessons
    private String videoUrl;

    // For QUIZ lessons
    private List<QuizQuestionDTO> quizQuestions;
}
