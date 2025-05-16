package com.toeic.dto.request;

import lombok.Data;

@Data
public class CreateCardMatchingPairRequest {
    private String prompt;
    private String answer;
    private Integer orderIndex;
} 