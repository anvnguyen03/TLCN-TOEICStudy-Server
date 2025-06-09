package com.toeic.service;

import java.util.List;

import com.toeic.dto.response.UserLearningDTO;

public interface CourseEnrollmentService {
    void enrollCourse(Long userId, Long courseId);
    Boolean isEnrolled(Long userId, Long courseId);
    List<UserLearningDTO> getUserCoursesEnrolled(Long userId);
}   
