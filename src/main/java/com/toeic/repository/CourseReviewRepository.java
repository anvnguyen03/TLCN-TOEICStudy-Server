package com.toeic.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.toeic.entity.CourseReview;

@Repository
public interface CourseReviewRepository extends JpaRepository<CourseReview, Long> {
    // Get 3 newest reviews
    List<CourseReview> findByCourseIdOrderByCreatedAtDesc(Long courseId, Pageable pageable);
}
