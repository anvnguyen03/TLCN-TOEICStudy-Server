package com.toeic.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
// return all course detail for admin course list usecase
public class FullCourseDTO {
    private Long id;
    private String title;
    private String description;
    private String objective;
    private String thumbnailUrl;
    private String previewVideoUrl;
    private double price;
    private String status;
    private String level;
    private List<FullCourseSectionDTO> sections;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}
