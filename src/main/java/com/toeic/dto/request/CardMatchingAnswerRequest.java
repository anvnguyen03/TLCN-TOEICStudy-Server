package com.toeic.dto.request;

import java.util.List;
import lombok.Data;

@Data
public class CardMatchingAnswerRequest {
    private Long quizQuestionId;
    private List<CardMatchingPair> pairs;
    
    @Data
    public static class CardMatchingPair {
        private Long promptId;
        private String answerContent;
    }
} 