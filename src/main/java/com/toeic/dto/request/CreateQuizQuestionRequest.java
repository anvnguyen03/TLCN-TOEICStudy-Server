package com.toeic.dto.request;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CreateQuizQuestionRequest {
    private String question;
    private String type;
    private Integer orderIndex;
    private CreateQuizQuestionOptionRequest option;
    
    @JsonProperty("pairs")
    private List<CreateCardMatchingPairRequest> pairs;
} 