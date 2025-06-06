package com.toeic.service;

import java.util.List;

import com.toeic.dto.request.CardMatchingAnswerRequest;
import com.toeic.dto.request.MultipleChoiceAnswerRequest;
import com.toeic.dto.response.LearnCardMatchingResultDTO;
import com.toeic.dto.response.LearnMultipleChoiceResultDTO;
import com.toeic.dto.response.TrialCardMatchingResultDTO;
import com.toeic.dto.response.TrialMultipleChoiceResultDTO;

public interface DoCourseService {
    // Check multiple choice quiz answers for trial
    TrialMultipleChoiceResultDTO checkTrialMultipleChoiceAnswers(List<MultipleChoiceAnswerRequest> answers);
    
    // Check card matching quiz answers for trial
    TrialCardMatchingResultDTO checkTrialCardMatchingAnswers(CardMatchingAnswerRequest request);

    // Check multiple choice quiz answers for enrolled user
    LearnMultipleChoiceResultDTO checkEnrolledMultipleChoiceAnswers(Long userId, Long lessonId, List<MultipleChoiceAnswerRequest> answers);

    // Check card matching quiz answers for enrolled user
    LearnCardMatchingResultDTO checkEnrolledCardMatchingAnswers(Long userId, Long lessonId, CardMatchingAnswerRequest request);
}
