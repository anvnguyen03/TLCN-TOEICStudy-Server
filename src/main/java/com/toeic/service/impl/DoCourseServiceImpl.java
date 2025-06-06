package com.toeic.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.toeic.dto.request.CardMatchingAnswerRequest;
import com.toeic.dto.request.MultipleChoiceAnswerRequest;
import com.toeic.dto.response.TrialCardMatchingResultDTO;
import com.toeic.dto.response.LearnCardMatchingResultDTO;
import com.toeic.dto.response.LearnMultipleChoiceResultDTO;
import com.toeic.dto.response.QuizAnswerResultDTO;
import com.toeic.dto.response.TrialMultipleChoiceResultDTO;
import com.toeic.entity.CardMatchingPair;
import com.toeic.entity.EQuizType;
import com.toeic.entity.LessonCompletion;
import com.toeic.entity.Lesson;
import com.toeic.entity.QuizQuestion;
import com.toeic.entity.QuizQuestionOption;
import com.toeic.entity.User;
import com.toeic.exception.ResourceNotFoundException;
import com.toeic.repository.LessonCompletionRepository;
import com.toeic.repository.QuizQuestionRepository;
import com.toeic.repository.UserRepository;
import com.toeic.repository.LessonRepository;
import com.toeic.service.DoCourseService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DoCourseServiceImpl implements DoCourseService {

    private final QuizQuestionRepository quizQuestionRepository;
    private final LessonCompletionRepository lessonCompletionRepository;
    private final UserRepository userRepository;
    private final LessonRepository lessonRepository;

    @Override
    @Transactional(readOnly = true)
    public TrialMultipleChoiceResultDTO checkTrialMultipleChoiceAnswers(List<MultipleChoiceAnswerRequest> answers) {
        TrialMultipleChoiceResultDTO result = new TrialMultipleChoiceResultDTO();
        List<QuizAnswerResultDTO> results = new ArrayList<>();
        int correctCount = 0;

        for (MultipleChoiceAnswerRequest answer : answers) {
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
    public TrialCardMatchingResultDTO checkTrialCardMatchingAnswers(CardMatchingAnswerRequest request) {
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
                        CardMatchingPair::getAnswer));

        for (CardMatchingAnswerRequest.CardMatchingPair submittedPair : request.getPairs()) {
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

    @Override
    public LearnMultipleChoiceResultDTO checkEnrolledMultipleChoiceAnswers(Long userId, Long lessonId,
            List<MultipleChoiceAnswerRequest> answers) {

        LearnMultipleChoiceResultDTO result = new LearnMultipleChoiceResultDTO();
        List<QuizAnswerResultDTO> results = new ArrayList<>();
        int correctCount = 0;

        for (MultipleChoiceAnswerRequest answer : answers) {
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

        result.setLessonId(lessonId);
        result.setTotalQuestions(answers.size());
        result.setCorrectAnswers(correctCount);
        result.setResults(results);

        if (lessonCompletionRepository.existsByUserIdAndLessonId(userId, lessonId)) {
            result.setCompleted(true);
        } else {
            // the lesson is completed if the user has correct all the questions in the
            // lesson
            if (correctCount == answers.size()) {
                result.setCompleted(true);
                User user = userRepository.findById(userId)
                        .orElseThrow(() -> new ResourceNotFoundException("User not found"));
                Lesson lesson = lessonRepository.findById(lessonId)
                        .orElseThrow(() -> new ResourceNotFoundException("Lesson not found"));

                LessonCompletion lessonCompletion = new LessonCompletion();
                lessonCompletion.setUser(user);
                lessonCompletion.setLesson(lesson);
                lessonCompletionRepository.save(lessonCompletion);
            } else {
                result.setCompleted(false);
            }
        }

        return result;
    }

    @Override
    public LearnCardMatchingResultDTO checkEnrolledCardMatchingAnswers(Long userId, Long lessonId,
            CardMatchingAnswerRequest request) {
        QuizQuestion question = quizQuestionRepository.findById(request.getQuizQuestionId())
                .orElseThrow(() -> new ResourceNotFoundException("Quiz question not found"));

        if (question.getType() != EQuizType.CARD_MATCHING) {
            throw new ResourceNotFoundException("Question is not a card matching type");
        }

        LearnCardMatchingResultDTO result = new LearnCardMatchingResultDTO();
        List<LearnCardMatchingResultDTO.CardMatchingPairResult> results = new ArrayList<>();
        int correctCount = 0;

        // Create a map of promptId -> correct answer content for easy lookup
        Map<Long, String> correctPairs = question.getPairs().stream()
                .collect(Collectors.toMap(
                        CardMatchingPair::getId,
                        CardMatchingPair::getAnswer));

        for (CardMatchingAnswerRequest.CardMatchingPair submittedPair : request.getPairs()) {
            String correctAnswer = correctPairs.get(submittedPair.getPromptId());
            boolean isCorrect = correctAnswer != null &&
                    correctAnswer.equalsIgnoreCase(submittedPair.getAnswerContent().trim());

            LearnCardMatchingResultDTO.CardMatchingPairResult pairResult = new LearnCardMatchingResultDTO.CardMatchingPairResult();
            pairResult.setPromptId(submittedPair.getPromptId());
            pairResult.setSelectedAnswer(submittedPair.getAnswerContent());
            pairResult.setCorrectAnswer(correctAnswer);
            pairResult.setCorrect(isCorrect);
            results.add(pairResult);

            if (isCorrect) {
                correctCount++;
            }
        }

        result.setLessonId(lessonId);
        result.setTotalPairs(request.getPairs().size());
        result.setCorrectPairs(correctCount);
        result.setResults(results);

        if (lessonCompletionRepository.existsByUserIdAndLessonId(userId, lessonId)) {
            result.setCompleted(true);
        } else {
            // the lesson is completed if the user has correct all the pairs in the lesson
            if (correctCount == request.getPairs().size()) {
                result.setCompleted(true);
                User user = userRepository.findById(userId)
                        .orElseThrow(() -> new ResourceNotFoundException("User not found"));
                Lesson lesson = lessonRepository.findById(lessonId)
                        .orElseThrow(() -> new ResourceNotFoundException("Lesson not found"));

                LessonCompletion lessonCompletion = new LessonCompletion();
                lessonCompletion.setUser(user);
                lessonCompletion.setLesson(lesson);
                lessonCompletionRepository.save(lessonCompletion);
            } else {
                result.setCompleted(false);
            }
        }

        return result;
    }
}
