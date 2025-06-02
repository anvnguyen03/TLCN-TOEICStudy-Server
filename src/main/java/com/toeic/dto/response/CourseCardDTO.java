package com.toeic.dto.response;

import lombok.Data;

@Data
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
