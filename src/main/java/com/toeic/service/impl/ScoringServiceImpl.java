package com.toeic.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.toeic.dto.request.SubmitAnswer;
import com.toeic.dto.request.SubmitFullTestRequest;
import com.toeic.dto.response.UserResultDTO;
import com.toeic.entity.EAttempStatus;
import com.toeic.entity.Question;
import com.toeic.entity.Test;
import com.toeic.entity.User;
import com.toeic.entity.UserAnswer;
import com.toeic.entity.UserResult;
import com.toeic.exception.ResourceNotFoundException;
import com.toeic.repository.ListeningScoreConversionRepository;
import com.toeic.repository.QuestionRepository;
import com.toeic.repository.ReadingScoreConversionRepository;
import com.toeic.repository.TestRepository;
import com.toeic.repository.UserAnswerRepository;
import com.toeic.repository.UserRepository;
import com.toeic.repository.UserResultRepository;
import com.toeic.service.ScoringService;
import com.toeic.utils.DTOMapperUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ScoringServiceImpl implements ScoringService {
	
	private final ListeningScoreConversionRepository listeningScoreRepository;
	private final ReadingScoreConversionRepository readingScoreRepository;
	private final UserResultRepository userResultRepository;
	private final UserAnswerRepository userAnswerRepository;
	private final TestRepository testRepository;
	private final QuestionRepository questionRepository;
	private final UserRepository userRepository;
	
	@Transactional
	@Override
	public UserResultDTO submitFullTest(SubmitFullTestRequest submitRequest) {
		
		UserResultDTO userResultDTO = new UserResultDTO();
		
		try {
			int correct = 0;
			int incorrect = 0;
			int skipped = 0;
			int listeningCorrect = 0;
			int readingCorrect = 0;
			UserResult userResult = new UserResult();
			List<UserAnswer> userAnswers = new ArrayList<>();
			
			User user = userRepository.findByEmail(submitRequest.getEmail())
					.orElseThrow(() -> new ResourceNotFoundException("Unknown user"));
			Test test = testRepository.findById(submitRequest.getTestId())
					.orElseThrow(() -> new ResourceNotFoundException("Unknown test"));
			userResult.setUser(user);
			userResult.setTest(test);
			userResult.setTest_mode(submitRequest.getTestMode());
			userResult.setAttemp_status(EAttempStatus.SUBMITTED);
			userResult.setCompleted_at(LocalDateTime.now());
			userResult.setCompletion_time(submitRequest.getCompletionTime());
			userResult = userResultRepository.save(userResult);
			
			List<SubmitAnswer> submitAnswers = submitRequest.getUserAnswers();
			
			for (SubmitAnswer submitAnswer : submitAnswers) {
				Question question = questionRepository.findById(submitAnswer.getQuestionId())
						.orElseThrow(() -> new ResourceNotFoundException("Unknown question"));
				UserAnswer userAnswer = new UserAnswer();
				userAnswer.setUserResult(userResult);
				userAnswer.setQuestion(question);
				if (submitAnswer.getAnswer() == null) {
					skipped++;
				} else if (submitAnswer.getAnswer().equals(question.getCorrect_answer())) {
					correct++;
					userAnswer.set_correct(true);
					userAnswer.setSelected_answer(submitAnswer.getAnswer());
					if (question.getOrder_number() <= 100) {
						listeningCorrect++;
					} else {
						readingCorrect++;
					}
				} else {
					incorrect++;
					userAnswer.set_correct(false);
					userAnswer.setSelected_answer(submitAnswer.getAnswer());
				}
				userAnswerRepository.save(userAnswer);
				userAnswers.add(userAnswer);
			}
			
			userResult.setUserAnswers(userAnswers);
			userResult.setCorrect_answers(correct);
			userResult.setIncorrect_answers(incorrect);
			userResult.setSkipped_answers(skipped);
			userResult.setListening_corrects(listeningCorrect);
			userResult.setReading_corrects(readingCorrect);
			float accuracy = (float) correct/200*100;
			userResult.setAccuracy(accuracy);
			int listeningScore = calculateListeningScore(listeningCorrect);
			int readingScore = calculateReadingScore(readingCorrect);
			userResult.setListening_score(listeningScore);
			userResult.setReading_score(readingScore);
			userResult.setTotal_score(listeningScore + readingScore);
			
			userResult = userResultRepository.save(userResult);
			userResultDTO = DTOMapperUtils.mapToUserResultDTO(userResult);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Error processing submited test", e);
		}
		return userResultDTO;
	}
	
	private int calculateListeningScore(int correctAnswers) {
		return listeningScoreRepository.findByCorrect(correctAnswers).get().getScore();
	}
	
	private int calculateReadingScore(int correctAnswers) {
		return readingScoreRepository.findByCorrect(correctAnswers).get().getScore();
	}

}
