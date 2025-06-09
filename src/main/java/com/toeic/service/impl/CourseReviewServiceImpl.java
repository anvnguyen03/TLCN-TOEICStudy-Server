package com.toeic.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.toeic.dto.request.CourseReviewRequest;
import com.toeic.dto.response.CourseReviewDTO;
import com.toeic.dto.response.CourseReviewPagingDTO;
import com.toeic.dto.response.CourseReviewStatistics;
import com.toeic.entity.CourseReview;
import com.toeic.entity.Course;
import com.toeic.entity.User;
import com.toeic.repository.CourseRepository;
import com.toeic.repository.CourseReviewRepository;
import com.toeic.service.CourseReviewService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CourseReviewServiceImpl implements CourseReviewService {

    private final CourseReviewRepository courseReviewRepository;
    private final CourseRepository courseRepository;

    @Override
    @Transactional
    public void createCourseReview(CourseReviewRequest courseReview, User user) {

        // check if user already reviewed this course
        if (courseReviewRepository.findByCourseIdAndUserId(courseReview.getCourseId(), user.getId()).isPresent()) {
            throw new RuntimeException("User already reviewed this course");
        }
        
        CourseReview newCourseReview = new CourseReview();
        newCourseReview.setCourse(courseRepository.findById(courseReview.getCourseId()).orElseThrow(() -> new RuntimeException("Course not found")));
        newCourseReview.setUser(user);
        newCourseReview.setRating(courseReview.getRating());
        newCourseReview.setComment(courseReview.getComment());
        courseReviewRepository.save(newCourseReview);
    }

    @Override
    public void updateCourseReview(CourseReviewRequest courseReview, User user) {
        // check if user is the owner of the course review
        Optional<CourseReview> existingCourseReview = courseReviewRepository.findByCourseIdAndUserId(courseReview.getCourseId(), user.getId());
        if (!existingCourseReview.isPresent()) {
            throw new RuntimeException("User is not the owner of the course review");
        }

        existingCourseReview.get().setRating(courseReview.getRating());
        existingCourseReview.get().setComment(courseReview.getComment());
        courseReviewRepository.save(existingCourseReview.get());
    }

    @Override
    public void deleteCourseReview(Long courseReviewId) {
        // check if course review exists
        Optional<CourseReview> existingCourseReview = courseReviewRepository.findById(courseReviewId);
        if (!existingCourseReview.isPresent()) {
            throw new RuntimeException("Course review not found");
        }
        courseReviewRepository.delete(existingCourseReview.get());
    }

    @Override
    public List<CourseReview> getCourseReviews(Long courseId, Pageable pageable) {
        return courseReviewRepository.findByCourseId(courseId, pageable).getContent();
    }

    @Override
    public CourseReviewStatistics getCourseReviewStatistics(Long courseId) {

        Course course = courseRepository.findById(courseId).orElseThrow(() -> new RuntimeException("Course not found"));
        List<CourseReview> courseReviews = course.getReviews();
        double averageRating = courseReviews.stream()
                .mapToDouble(CourseReview::getRating)
                .average()
                .orElse(0);

        double numberOfOneStar = (double) courseReviews.stream().filter(review -> review.getRating() == 1).count();
        double oneStarRating = Math.round((numberOfOneStar / courseReviews.size()) * 100.0);

        double numberOfTwoStar = (double) courseReviews.stream().filter(review -> review.getRating() == 2).count();
        double twoStarRating = Math.round((numberOfTwoStar / courseReviews.size()) * 100.0);

        double numberOfThreeStar = (double) courseReviews.stream().filter(review -> review.getRating() == 3).count();
        double threeStarRating = Math.round((numberOfThreeStar / courseReviews.size()) * 100.0);

        double numberOfFourStar = (double) courseReviews.stream().filter(review -> review.getRating() == 4).count();
        double fourStarRating = Math.round((numberOfFourStar / courseReviews.size()) * 100.0);

        double numberOfFiveStar = (double) courseReviews.stream().filter(review -> review.getRating() == 5).count();
        double fiveStarRating = Math.round((numberOfFiveStar / courseReviews.size()) * 100.0);

        CourseReviewStatistics courseReviewStatistics = new CourseReviewStatistics();
        courseReviewStatistics.setAverageRating(averageRating);
        courseReviewStatistics.setOneStarRating(oneStarRating);
        courseReviewStatistics.setTwoStarRating(twoStarRating);
        courseReviewStatistics.setThreeStarRating(threeStarRating);
        courseReviewStatistics.setFourStarRating(fourStarRating);
        courseReviewStatistics.setFiveStarRating(fiveStarRating);
        return courseReviewStatistics;
    }

    @Override
    public CourseReviewPagingDTO getCourseReviewsPagination(Long courseId, Pageable pageable) {
        Page<CourseReview> courseReviewPage = courseReviewRepository.findByCourseId(courseId, pageable);
        
        List<CourseReviewDTO> reviewDTOs = courseReviewPage.getContent().stream()
            .map(review -> {
                CourseReviewDTO dto = new CourseReviewDTO();
                dto.setId(review.getId());
                dto.setRating(review.getRating());
                dto.setComment(review.getComment());
                dto.setUsername(review.getUser().getFullname());
                dto.setCreatedAt(review.getCreatedAt());
                return dto;
            })
            .collect(Collectors.toList());

        CourseReviewPagingDTO pagingDTO = new CourseReviewPagingDTO();
        pagingDTO.setReviews(reviewDTOs);
        pagingDTO.setTotalPages(courseReviewPage.getTotalPages());
        pagingDTO.setTotalElements(courseReviewPage.getTotalElements());
        
        return pagingDTO;
    }
}
