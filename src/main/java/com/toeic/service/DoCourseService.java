package com.toeic.service;

import java.util.List;

import com.toeic.dto.request.TrialCardMatchingAnswerRequest;
import com.toeic.dto.request.TrialMultipleChoiceAnswerRequest;
import com.toeic.dto.response.TrialCardMatchingResultDTO;
import com.toeic.dto.response.TrialMultipleChoiceResultDTO;

public interface DoCourseService {
    // Check multiple choice quiz answers for trial
    TrialMultipleChoiceResultDTO checkTrialMultipleChoiceAnswers(List<TrialMultipleChoiceAnswerRequest> answers);
    
    // Check card matching quiz answers for trial
    TrialCardMatchingResultDTO checkTrialCardMatchingAnswers(TrialCardMatchingAnswerRequest request);
}
