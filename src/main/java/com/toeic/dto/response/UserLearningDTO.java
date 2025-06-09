package com.toeic.dto.response;

import lombok.Data;

@Data
public class UserLearningDTO {
    private Long id;
    private String thumbnailUrl;
    private String title;
    private String level;
    private double progress;
    private int rating;
    private String review;
}
