package com.toeic.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.toeic.dto.request.TrialCardMatchingAnswerRequest;
import com.toeic.dto.request.TrialMultipleChoiceAnswerRequest;
import com.toeic.dto.response.TrialCardMatchingResultDTO;
import com.toeic.dto.response.QuizAnswerResultDTO;
import com.toeic.dto.response.TrialMultipleChoiceResultDTO;
import com.toeic.entity.CardMatchingPair;
import com.toeic.entity.EQuizType;
import com.toeic.entity.QuizQuestion;
import com.toeic.entity.QuizQuestionOption;
import com.toeic.exception.ResourceNotFoundException;
import com.toeic.repository.QuizQuestionRepository;
import com.toeic.service.DoCourseService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DoCourseServiceImpl implements DoCourseService {

    private final QuizQuestionRepository quizQuestionRepository;

    @Override
    @Transactional(readOnly = true)
    public TrialMultipleChoiceResultDTO checkTrialMultipleChoiceAnswers(List<TrialMultipleChoiceAnswerRequest> answers) {
        TrialMultipleChoiceResultDTO result = new TrialMultipleChoiceResultDTO();
        List<QuizAnswerResultDTO> results = new ArrayList<>();
        int correctCount = 0;

        for (TrialMultipleChoiceAnswerRequest answer : answers) {
            QuizQuestion question = quizQuestionRepository.findById(answer.getQuizQuestionId())
                .orElseThrow(() -> new ResourceNotFoundException("Quiz question not found"));

            QuizQuestionOption option = question.getOption();
            boolean isCorrect = answer.getSelectedOption().equals(option.getCorrectOption());

            QuizAnswerResultDTO answerResult = new QuizAnswerResultDTO();
            answerResult.setQuizQuestionId(question.getId());
            answerResult.setCorrect(isCorrect);
            answerResult.setCorrectOption(option.getCorrectOption());
            answerResult.setSelectedOption(answer.getSelectedOption());
            results.add(answerResult);

            if (isCorrect) {
                correctCount++;
            }
        }

        result.setTotalQuestions(answers.size());
        result.setCorrectAnswers(correctCount);
        result.setResults(results);

        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public TrialCardMatchingResultDTO checkTrialCardMatchingAnswers(TrialCardMatchingAnswerRequest request) {
        QuizQuestion question = quizQuestionRepository.findById(request.getQuizQuestionId())
            .orElseThrow(() -> new ResourceNotFoundException("Quiz question not found"));

        if (question.getType() != EQuizType.CARD_MATCHING) {
            throw new ResourceNotFoundException("Question is not a card matching type");
        }

        TrialCardMatchingResultDTO result = new TrialCardMatchingResultDTO();
        List<TrialCardMatchingResultDTO.CardMatchingPairResult> results = new ArrayList<>();
        int correctCount = 0;

        // Create a map of promptId -> correct answer content for easy lookup
        Map<Long, String> correctPairs = question.getPairs().stream()
            .collect(Collectors.toMap(
                CardMatchingPair::getId,
                CardMatchingPair::getAnswer
            ));

        for (TrialCardMatchingAnswerRequest.CardMatchingPair submittedPair : request.getPairs()) {
            String correctAnswer = correctPairs.get(submittedPair.getPromptId());
            boolean isCorrect = correctAnswer != null && 
                              correctAnswer.equalsIgnoreCase(submittedPair.getAnswerContent().trim());

            TrialCardMatchingResultDTO.CardMatchingPairResult pairResult = new TrialCardMatchingResultDTO.CardMatchingPairResult();
            pairResult.setPromptId(submittedPair.getPromptId());
            pairResult.setSelectedAnswer(submittedPair.getAnswerContent());
            pairResult.setCorrectAnswer(correctAnswer);
            pairResult.setCorrect(isCorrect);
            results.add(pairResult);

            if (isCorrect) {
                correctCount++;
            }
        }

        result.setTotalPairs(request.getPairs().size());
        result.setCorrectPairs(correctCount);
        result.setResults(results);

        return result;
    }
}
