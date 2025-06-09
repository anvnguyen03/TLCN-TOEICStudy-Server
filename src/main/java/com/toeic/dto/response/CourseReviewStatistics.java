package com.toeic.dto.response;

import lombok.Data;

@Data
public class CourseReviewStatistics {
    private double averageRating;
    private double oneStarRating;
    private double twoStarRating;
    private double threeStarRating;
    private double fourStarRating;
    private double fiveStarRating;
}
