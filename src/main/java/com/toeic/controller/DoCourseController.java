package com.toeic.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.toeic.dto.request.CardMatchingAnswerRequest;
import com.toeic.dto.request.MultipleChoiceAnswerRequest;
import com.toeic.dto.response.ApiResponse;
import com.toeic.dto.response.CourseDetailDTO;
import com.toeic.dto.response.LearnCardMatchingResultDTO;
import com.toeic.dto.response.LearnMultipleChoiceResultDTO;
import com.toeic.dto.response.TrialCardMatchingResultDTO;
import com.toeic.dto.response.TrialMultipleChoiceResultDTO;
import com.toeic.service.CourseService;
import com.toeic.service.DoCourseService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/do-course")
@CrossOrigin(origins = "*")
public class DoCourseController {
    
    private final DoCourseService doCourseService;
    private final CourseService courseService;

    @PostMapping("/trial/multiple-choice/check-answers")
    public ResponseEntity<ApiResponse<TrialMultipleChoiceResultDTO>> checkTrialMultipleChoiceAnswers(
            @RequestBody List<MultipleChoiceAnswerRequest> answers) {
        TrialMultipleChoiceResultDTO result = doCourseService.checkTrialMultipleChoiceAnswers(answers);
        ApiResponse<TrialMultipleChoiceResultDTO> response = ApiResponse.success(
            HttpStatus.OK, "Multiple choice quiz answers checked successfully", result);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/trial/card-matching/check-answers")
    public ResponseEntity<ApiResponse<TrialCardMatchingResultDTO>> checkTrialCardMatchingAnswers(
            @RequestBody CardMatchingAnswerRequest request) {
        TrialCardMatchingResultDTO result = doCourseService.checkTrialCardMatchingAnswers(request);
        ApiResponse<TrialCardMatchingResultDTO> response = ApiResponse.success(
            HttpStatus.OK, "Card matching answers checked successfully", result);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{courseId}/complete-course-detail")
    public ResponseEntity<ApiResponse<CourseDetailDTO>> getCompleteCourseDetail(
            @PathVariable Long courseId,
            @RequestParam Long userId) {
        CourseDetailDTO result = courseService.getCompleteCourseDetail(courseId, userId);
        ApiResponse<CourseDetailDTO> response = ApiResponse.success(
            HttpStatus.OK, "Complete course detail fetched successfully", result);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/enrolled/multiple-choice/check-answers")
    public ResponseEntity<ApiResponse<LearnMultipleChoiceResultDTO>> checkEnrolledMultipleChoiceAnswers(
            @RequestBody List<MultipleChoiceAnswerRequest> answers,
            @RequestParam Long userId,
            @RequestParam Long lessonId) {
        LearnMultipleChoiceResultDTO result = doCourseService.checkEnrolledMultipleChoiceAnswers(userId, lessonId, answers);
        ApiResponse<LearnMultipleChoiceResultDTO> response = ApiResponse.success(
            HttpStatus.OK, "Multiple choice quiz answers checked successfully", result);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/enrolled/card-matching/check-answers")
    public ResponseEntity<ApiResponse<LearnCardMatchingResultDTO>> checkEnrolledCardMatchingAnswers(
            @RequestBody CardMatchingAnswerRequest request,
            @RequestParam Long userId,
            @RequestParam Long lessonId) {
        LearnCardMatchingResultDTO result = doCourseService.checkEnrolledCardMatchingAnswers(userId, lessonId, request);
        ApiResponse<LearnCardMatchingResultDTO> response = ApiResponse.success(
            HttpStatus.OK, "Card matching answers checked successfully", result);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/enrolled/mark-lesson-as-completed")
    public ResponseEntity<ApiResponse<Long>> markLessonAsCompleted(
            @RequestParam Long userId,
            @RequestParam Long lessonId) {
        doCourseService.markLessonAsCompleted(userId, lessonId);
        ApiResponse<Long> response = ApiResponse.success(HttpStatus.OK, "Lesson marked as completed", lessonId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/enrolled/unmark-lesson-as-completed")
    public ResponseEntity<ApiResponse<Long>> unmarkLessonAsCompleted(
            @RequestParam Long userId,
            @RequestParam Long lessonId) {
        doCourseService.unmarkLessonAsCompleted(userId, lessonId);
        ApiResponse<Long> response = ApiResponse.success(HttpStatus.OK, "Lesson unmarked as completed", lessonId);
        return ResponseEntity.ok(response);
    }
}
