package com.toeic.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.toeic.dto.response.CourseCardDTO;
import com.toeic.dto.response.CourseDetailDTO;
import com.toeic.dto.response.CourseInfoDTO;
import com.toeic.dto.response.CourseReviewDTO;
import com.toeic.dto.response.LessonDetailDTO;
import com.toeic.entity.Course;
import com.toeic.entity.CourseReview;
import com.toeic.entity.ECourseStatus;
import com.toeic.entity.Lesson;
import com.toeic.exception.ResourceNotFoundException;
import com.toeic.repository.CourseRepository;
import com.toeic.repository.CourseReviewRepository;
import com.toeic.repository.LessonCompletionRepository;
import com.toeic.service.CourseService;
import com.toeic.utils.DTOMapperUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final CourseReviewRepository courseReviewRepository;
    private final LessonCompletionRepository lessonCompletionRepository;

    @Override
    public List<CourseCardDTO> getAllCourses() {
        List<Course> courses = courseRepository.findAll();
        List<CourseCardDTO> courseCardDTOs = courses.stream()
                .map(course -> {
                    CourseCardDTO dto = DTOMapperUtils.mapToCourseCardDTO(course);
                    // Calculate average rating
                    double avgRating = course.getReviews().stream()
                            .mapToDouble(CourseReview::getRating)
                            .average()
                            .orElse(0);
                    dto.setRating(avgRating);
                    return dto;
                })
                .collect(Collectors.toList());

        return courseCardDTOs;
    }

    @Override
    public List<CourseCardDTO> getPublishedCourses() {
        List<Course> courses = courseRepository.findByStatus(ECourseStatus.PUBLISHED);
        return courses.stream()
                .map(course -> {
                    CourseCardDTO dto = DTOMapperUtils.mapToCourseCardDTO(course);
                    // Calculate average rating
                    double avgRating = course.getReviews().stream()
                            .mapToDouble(CourseReview::getRating)
                            .average()
                            .orElse(0);
                    dto.setRating(avgRating);
                    
                    // Calculate total lessons
                    int totalLessons = course.getSections().stream()
                            .mapToInt(section -> section.getLessons().size())
                            .sum();
                    dto.setLessons(totalLessons);
                    
                    // Calculate total students
                    int totalStudents = course.getEnrollments().size();
                    dto.setStudents(totalStudents);
    
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public CourseInfoDTO getCourseById(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));
        
        CourseInfoDTO dto = DTOMapperUtils.mapToCourseInfoDTO(course);

        // Calculate rating
        double rating = course.getReviews().stream()
                .mapToDouble(CourseReview::getRating)
                .average()
                .orElse(0);
        dto.setRating(rating);

        // Calculate duration
        int duration = course.getSections().stream()
                .mapToInt(section -> section.getLessons().stream()
                        .mapToInt(Lesson::getDuration)
                        .sum())
                .sum();
        dto.setDuration(duration);

        return dto;
    }

    @Override
    public List<CourseReviewDTO> getCourseRecentReviews(Long courseId) {
        List<CourseReview> reviews = courseReviewRepository.findByCourseIdOrderByCreatedAtDesc(courseId, Pageable.ofSize(3));
        return reviews.stream()
                .map(DTOMapperUtils::mapToCourseReviewDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<LessonDetailDTO> getFreeLessonsOfCourse(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));

        List<Lesson> freeLessons = course.getSections().stream()
                .flatMap(section -> section.getLessons().stream())
                .filter(lesson -> lesson.getIsFree())
                .collect(Collectors.toList());

        // map to DTO
        List<LessonDetailDTO> lessonDetailDTOs = freeLessons.stream()
                .map(DTOMapperUtils::mapToLessonDetailDTO)
                .collect(Collectors.toList());

        return lessonDetailDTOs;

    }

    @Override   
    public CourseDetailDTO getCompleteCourseDetail(Long courseId, Long userId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));

        CourseDetailDTO dto = DTOMapperUtils.mapToCourseDetailDTO(course);

        // Calculate total completed lessons and set completion status
        int totalCompletedLessons = dto.getSections().stream()
            .flatMap(section -> section.getLessons().stream())
            .mapToInt(lesson -> {
                boolean isCompleted = lessonCompletionRepository.existsByUserIdAndLessonId(userId, lesson.getId());
                lesson.setIsCompleted(isCompleted);
                return isCompleted ? 1 : 0;
            })
            .sum();
        dto.setTotalCompletedLessons(totalCompletedLessons);
        return dto;
    }

}