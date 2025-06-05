package com.toeic.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.toeic.entity.Course;
import com.toeic.entity.CourseEnrollment;
import com.toeic.entity.EEnrollmentStatus;
import com.toeic.entity.User;
import com.toeic.exception.ResourceNotFoundException;
import com.toeic.exception.UserNotFoundException;
import com.toeic.repository.CourseEnrollmentRepository;
import com.toeic.repository.CourseRepository;
import com.toeic.repository.UserRepository;
import com.toeic.service.CourseEnrollmentService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CourseEnrollmentServiceImpl implements CourseEnrollmentService {
    
    private final CourseEnrollmentRepository courseEnrollmentRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;

    @Override
    public void enrollCourse(Long userId, Long courseId) {
        User user = userRepository.findById(userId)
        .orElseThrow(() -> new UserNotFoundException("User not found"));
        Course course = courseRepository.findById(courseId)
        .orElseThrow(() -> new ResourceNotFoundException("Course not found"));

        // check if user already enrolled in course
        if (courseEnrollmentRepository.findByUserIdAndCourseId(userId, courseId).isPresent()) {
            throw new RuntimeException("User already enrolled in course");
        }

        CourseEnrollment courseEnrollment = new CourseEnrollment();
        courseEnrollment.setUser(user);
        courseEnrollment.setCourse(course);
        courseEnrollment.setStatus(EEnrollmentStatus.ACTIVE);
        courseEnrollmentRepository.save(courseEnrollment);
    }

    @Override
    public List<CourseEnrollment> getEnrollmentsByUser(Long userId) {
        User user = userRepository.findById(userId)
        .orElseThrow(() -> new UserNotFoundException("User not found"));
        return courseEnrollmentRepository.findByUser(user);
    }

    @Override
    public Boolean isEnrolled(Long userId, Long courseId) {
        return courseEnrollmentRepository.findByUserIdAndCourseId(userId, courseId).isPresent();
    }
}
