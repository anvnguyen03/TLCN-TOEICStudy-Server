package com.toeic.dto.response;

import java.util.List;

import lombok.Data;

@Data
public class ReviewQuestionDTO {

	private long id;
	private int partNum;
	private int orderNumber;
	private String content;
	private String answer1;
	private String answer2;
	private String answer3;
	private String answer4;
	private List<String> images;
	private String groupContent;
	private String audio;
	private float startTimestamp;
	
	private String correctAnswer;
	private String transcript;
}
