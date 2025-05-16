package com.toeic.service;

import java.io.IOException;
import java.util.List;

import com.toeic.dto.request.CreateCourseRequest;
import com.toeic.dto.response.CourseCardDTO;
import com.toeic.dto.response.CourseDetailDTO;
import com.toeic.dto.response.CourseInfoDTO;
import com.toeic.dto.response.CourseReviewDTO;
import com.toeic.dto.response.FullCourseDTO;
import com.toeic.dto.response.LessonDetailDTO;

public interface CourseService {
    CourseInfoDTO createCourse(CreateCourseRequest request) throws IOException;
    List<FullCourseDTO> getAllCoursesDetailForAdmin();
    List<CourseCardDTO> getPublishedCourses();
    CourseInfoDTO getCourseById(Long courseId);
    List<CourseReviewDTO> getCourseRecentReviews(Long courseId);
    // Get free lessons of a course
    List<LessonDetailDTO> getFreeLessonsOfCourse(Long courseId);
    // Get a complete course detail for a user enrolled in the course
    CourseDetailDTO getCompleteCourseDetail(Long courseId, Long userId);
}
