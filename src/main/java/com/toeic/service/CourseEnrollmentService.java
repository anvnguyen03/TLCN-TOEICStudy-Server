package com.toeic.service;

import java.util.List;

import com.toeic.entity.CourseEnrollment;

public interface CourseEnrollmentService {
    void enrollCourse(Long userId, Long courseId);
    Boolean isEnrolled(Long userId, Long courseId);
    List<CourseEnrollment> getEnrollmentsByUser(Long userId);
}   
