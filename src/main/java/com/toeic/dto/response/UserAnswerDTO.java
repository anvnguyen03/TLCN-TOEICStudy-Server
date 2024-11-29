package com.toeic.dto.response;

import lombok.Data;

@Data
public class UserAnswerDTO {

	private long id;
	private long userResultId;
	private String selectedAnswer;
	// Jackson nhận diện getter isCorrect() tự động ánh xạ thành correct => trường trong JSON thay đổi thành correct
	private boolean isCorrect;
	private ReviewQuestionDTO question;
}
