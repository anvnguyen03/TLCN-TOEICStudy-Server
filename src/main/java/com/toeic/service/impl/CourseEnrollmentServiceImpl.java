package com.toeic.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.toeic.dto.response.UserLearningDTO;
import com.toeic.entity.Course;
import com.toeic.entity.CourseEnrollment;
import com.toeic.entity.CourseReview;
import com.toeic.entity.EEnrollmentStatus;
import com.toeic.entity.Lesson;
import com.toeic.entity.LessonCompletion;
import com.toeic.entity.User;
import com.toeic.exception.ResourceNotFoundException;
import com.toeic.exception.UserNotFoundException;
import com.toeic.repository.CourseEnrollmentRepository;
import com.toeic.repository.CourseRepository;
import com.toeic.repository.CourseReviewRepository;
import com.toeic.repository.LessonCompletionRepository;
import com.toeic.repository.UserRepository;
import com.toeic.service.CourseEnrollmentService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CourseEnrollmentServiceImpl implements CourseEnrollmentService {

    private final CourseEnrollmentRepository courseEnrollmentRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final LessonCompletionRepository lessonCompletionRepository;
    private final CourseReviewRepository courseReviewRepository;

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
    public List<UserLearningDTO> getUserCoursesEnrolled(Long userId) {
        List<CourseEnrollment> courseEnrollments = courseEnrollmentRepository.findByUserId(userId);
        List<UserLearningDTO> userLearningInfo = new ArrayList<>();
        for (CourseEnrollment courseEnrollment : courseEnrollments) {
            UserLearningDTO userLearningDTO = new UserLearningDTO();
            userLearningDTO.setId(courseEnrollment.getCourse().getId());
            userLearningDTO.setThumbnailUrl(courseEnrollment.getCourse().getThumbnailUrl());
            userLearningDTO.setTitle(courseEnrollment.getCourse().getTitle());
            userLearningDTO.setLevel(courseEnrollment.getCourse().getLevel().name());

            // calculate progress by counting completed lessons / total lessons
            int totalLessons = courseEnrollment.getCourse().getSections().stream()
                    .mapToInt(section -> section.getLessons().size())
                    .sum();
            int totalCompletedLessons = courseEnrollment.getCourse().getSections().stream()
                    .flatMap(section -> section.getLessons().stream())
                    .mapToInt(lesson -> {
                        boolean isCompleted = lessonCompletionRepository.existsByUserIdAndLessonId(userId,
                                lesson.getId());
                        return isCompleted ? 1 : 0;
                    })
                    .sum();
            double progress = (double) totalCompletedLessons / totalLessons * 100;
            userLearningDTO.setProgress(Math.round(progress * 100) / 100.0);

            Optional<CourseReview> courseReview = courseReviewRepository.findByCourseIdAndUserId(courseEnrollment.getCourse().getId(), userId);
            if (courseReview.isPresent()) {
                userLearningDTO.setRating(courseReview.get().getRating());
                userLearningDTO.setReview(courseReview.get().getComment());
            }

            userLearningInfo.add(userLearningDTO);
        }
        return userLearningInfo;
    }

    @Override
    public Boolean isEnrolled(Long userId, Long courseId) {
        return courseEnrollmentRepository.findByUserIdAndCourseId(userId, courseId).isPresent();
    }
}
