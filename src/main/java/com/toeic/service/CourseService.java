package com.toeic.service;

import java.util.List;

import com.toeic.dto.response.CourseCardDTO;
import com.toeic.dto.response.CourseInfoDTO;
import com.toeic.dto.response.CourseReviewDTO;
import com.toeic.dto.response.LessonDetailDTO;

public interface CourseService {
    List<CourseCardDTO> getAllCourses();
    List<CourseCardDTO> getPublishedCourses();
    CourseInfoDTO getCourseById(Long courseId);
    List<CourseReviewDTO> getCourseRecentReviews(Long courseId);
    // Get free lessons of a course
    List<LessonDetailDTO> getFreeLessonsOfCourse(Long courseId);
}
