package com.toeic.dto.request;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CreateSectionRequest {
    private String title;
    private Integer orderIndex;
    
    @JsonProperty("lessons")
    private List<CreateLessonRequest> lessons;
} 