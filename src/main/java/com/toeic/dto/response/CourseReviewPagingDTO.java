package com.toeic.dto.response;

import java.util.List;

import lombok.Data;

@Data
public class CourseReviewPagingDTO {
    private List<CourseReviewDTO> reviews;
    private int totalPages;
    private long totalElements;
}
