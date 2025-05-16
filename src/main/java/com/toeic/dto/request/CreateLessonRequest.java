package com.toeic.dto.request;

import java.util.List;
import org.springframework.web.multipart.MultipartFile;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CreateLessonRequest {
    private String title;
    private String description;
    private String type;
    private Integer duration;
    private Integer orderIndex;
    private Boolean isFree;
    private String content;
    private MultipartFile video;
    
    @JsonProperty("quizQuestions")
    private List<CreateQuizQuestionRequest> quizQuestions;
} 