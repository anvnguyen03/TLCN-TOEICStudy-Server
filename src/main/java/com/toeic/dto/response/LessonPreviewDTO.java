package com.toeic.dto.response;

import com.toeic.entity.ELessonType;

import lombok.Data;

@Data
public class LessonPreviewDTO {
    private Long id;
    private String title;
    private int orderIndex;
    private int duration;
    private boolean isFree;
    private ELessonType type;
}
