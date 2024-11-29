package com.toeic.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class UserResultDTO {

	private long id;
	private int correctAnswers;
	private int incorrectAnswers;
	private int skippedAnswers;
	private int listeningCorrects;
    private int readingCorrects;
    private int listeningScore;
    private int readingScore;
    private int totalScore;
    private int completionTime; // in seconds
    private float accuracy;
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime completedAt;
    private String attempStatus;
    private String testMode;
    private long userId;
    private long testId;
    private String testTitle;
    private List<UserAnswerDTO> userAnswers;
	
}
