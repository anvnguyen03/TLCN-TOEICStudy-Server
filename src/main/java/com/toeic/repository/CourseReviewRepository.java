package com.toeic.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.toeic.entity.CourseReview;

@Repository
public interface CourseReviewRepository extends JpaRepository<CourseReview, Long> {
    // Get 3 newest reviews
    List<CourseReview> findByCourseIdOrderByCreatedAtDesc(Long courseId, Pageable pageable);
    Optional<CourseReview> findByCourseIdAndUserId(Long courseId, Long userId);
    List<CourseReview> findByCourseId(Long courseId);
    Page<CourseReview> findByCourseId(Long courseId, Pageable pageable);
}
