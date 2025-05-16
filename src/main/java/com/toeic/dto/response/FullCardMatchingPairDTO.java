package com.toeic.dto.response;

import lombok.Data;

@Data
// return card matching pair detail for admin course list usecase
public class FullCardMatchingPairDTO {
    private Long id;
    private String prompt;
    private String answer;
    private int orderIndex;
}
