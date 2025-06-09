package com.toeic.service;

import java.util.List;

import com.toeic.dto.request.CourseReviewRequest;
import com.toeic.dto.response.CourseReviewPagingDTO;
import com.toeic.dto.response.CourseReviewStatistics;
import com.toeic.entity.CourseReview;
import com.toeic.entity.User;

import org.springframework.data.domain.Pageable;

public interface CourseReviewService {
    void createCourseReview(CourseReviewRequest courseReview, User user);
    void updateCourseReview(CourseReviewRequest courseReview, User user);
    void deleteCourseReview(Long courseReviewId);
    List<CourseReview> getCourseReviews(Long courseId, Pageable pageable);
    CourseReviewStatistics getCourseReviewStatistics(Long courseId);
    CourseReviewPagingDTO getCourseReviewsPagination(Long courseId, Pageable pageable);
}
