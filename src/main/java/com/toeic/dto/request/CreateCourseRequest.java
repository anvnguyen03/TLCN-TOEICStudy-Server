package com.toeic.dto.request;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.toeic.entity.ECourseLevel;
import com.toeic.entity.ECourseStatus;

import lombok.Data;

@Data
public class CreateCourseRequest {
    private String title;
    private String description;
    private String objective;
    private MultipartFile thumbnail;
    private String previewVideoUrl;
    private BigDecimal price;
    private ECourseStatus status;
    private ECourseLevel level;
    
    @JsonProperty("sections")
    private List<CreateSectionRequest> sections;
}