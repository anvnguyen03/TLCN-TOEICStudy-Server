package com.toeic.dto.response;

import java.util.List;
import lombok.Data;

@Data
public class AITestAnalysisDTO {
    private String overallFeedback;
    private String partAnalysis;
    private String skillAnalysis;
    private String improvementSuggestions;
    private List<String> strengths;
    private List<String> weaknesses;
}