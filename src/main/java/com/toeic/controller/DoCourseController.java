package com.toeic.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.toeic.dto.request.TrialCardMatchingAnswerRequest;
import com.toeic.dto.request.TrialMultipleChoiceAnswerRequest;
import com.toeic.dto.response.ApiResponse;
import com.toeic.dto.response.TrialCardMatchingResultDTO;
import com.toeic.dto.response.TrialMultipleChoiceResultDTO;
import com.toeic.service.DoCourseService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/do-course")
@CrossOrigin(origins = "*")
public class DoCourseController {
    
    private final DoCourseService doCourseService;

    @PostMapping("/trial/multiple-choice/check-answers")
    public ResponseEntity<ApiResponse<TrialMultipleChoiceResultDTO>> checkTrialMultipleChoiceAnswers(
            @RequestBody List<TrialMultipleChoiceAnswerRequest> answers) {
        TrialMultipleChoiceResultDTO result = doCourseService.checkTrialMultipleChoiceAnswers(answers);
        ApiResponse<TrialMultipleChoiceResultDTO> response = ApiResponse.success(
            HttpStatus.OK, "Multiple choice quiz answers checked successfully", result);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/trial/card-matching/check-answers")
    public ResponseEntity<ApiResponse<TrialCardMatchingResultDTO>> checkTrialCardMatchingAnswers(
            @RequestBody TrialCardMatchingAnswerRequest request) {
        TrialCardMatchingResultDTO result = doCourseService.checkTrialCardMatchingAnswers(request);
        ApiResponse<TrialCardMatchingResultDTO> response = ApiResponse.success(
            HttpStatus.OK, "Card matching answers checked successfully", result);
        return ResponseEntity.ok(response);
    }
}
