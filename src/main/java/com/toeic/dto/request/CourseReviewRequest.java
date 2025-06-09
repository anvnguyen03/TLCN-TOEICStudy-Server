package com.toeic.dto.request;

import lombok.Data;

@Data
public class CourseReviewRequest {
    private Long courseId;
    private int rating;
    private String comment;
}
