package com.toeic.dto.request;

import lombok.Data;

@Data
public class SubmitAnswer {

	private long questionId;
	private String answer;
}
