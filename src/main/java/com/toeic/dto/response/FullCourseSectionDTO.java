package com.toeic.dto.response;

import lombok.Data;
import java.util.List;

@Data
// return course section detail for admin course list usecase
public class FullCourseSectionDTO {
    private Long id;
    private String title;
    private int orderIndex;
    private List<FullLessonDTO> lessons;
}
