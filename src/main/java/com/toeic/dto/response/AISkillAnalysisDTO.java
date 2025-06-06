package com.toeic.dto.response;

import lombok.Data;

@Data
public class AISkillAnalysisDTO {
    private String skillName;
    private double proficiency;
    private String feedback;
} 