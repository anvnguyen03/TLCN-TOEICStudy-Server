package com.toeic.dto.response;

import java.util.List;

import lombok.Data;

@Data
// return course detail for course learn usecase
public class CourseDetailDTO {
    private Long id;
    private String title;
    private double rating;
    private int totalReviews;
    private int students;
    private String objective;
    private String description;
    private String previewVideoUrl;
    private String thumbnailUrl;
    private double price;
    private int duration;
    private int totalSections;
    private int totalLessons;
    private int totalCompletedLessons;
    private List<CourseSectionDetailDTO> sections;
}
