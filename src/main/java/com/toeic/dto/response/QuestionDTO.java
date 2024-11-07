package com.toeic.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class QuestionDTO {

	private long id;
	private int partNum;
	private int orderNumber;
	private String content;
	private String answer1;
	private String answer2;
	private String answer3;
	private String answer4;
	private String image;
	private String audio;
	private float startTimestamp;
	
}
