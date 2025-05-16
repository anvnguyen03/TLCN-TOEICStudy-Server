package com.toeic.dto.response;

import java.util.List;

import lombok.Data;

@Data
// return quiz question detail for admin course list usecase
public class FullQuizQuestionDTO {
    private Long id;
    private String question;
    private String type;
    private int orderIndex;
    private FullQuizQuestionOptionDTO option;
    private List<FullCardMatchingPairDTO> pairs;
}
