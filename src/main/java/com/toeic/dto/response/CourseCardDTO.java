package com.toeic.dto.response;

import lombok.Data;

@Data
// return course card for course list usecase
public class CourseCardDTO {
    private Long id;
    private String title;
    private int lessons;
    private int students;
    private String level;
    private double rating;
    private double price;
    private String image;
}
