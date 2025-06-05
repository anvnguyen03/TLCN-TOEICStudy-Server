package com.toeic.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.toeic.entity.CourseEnrollment;
import com.toeic.entity.User;

@Repository
public interface CourseEnrollmentRepository extends JpaRepository<CourseEnrollment, Long> {
    List<CourseEnrollment> findByUser(User user);
    Optional<CourseEnrollment> findByUserIdAndCourseId(Long userId, Long courseId);
}
