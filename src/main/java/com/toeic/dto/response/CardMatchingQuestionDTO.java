package com.toeic.dto.response;

import java.util.List;

import lombok.Data;

@Data
public class CardMatchingQuestionDTO {
    private List<MatchingPromptDTO> prompts;
    private List<MatchingAnswerDTO> answers;
}
