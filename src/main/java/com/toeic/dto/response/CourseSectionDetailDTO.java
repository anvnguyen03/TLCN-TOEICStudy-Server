package com.toeic.dto.response;

import java.util.List;

import lombok.Data; 

@Data
public class CourseSectionDetailDTO {
    private Long id;
    private String title;
    private int orderIndex;
    private int totalLessons;
    private int duration;
    private List<LessonDetailDTO> lessons;
}
