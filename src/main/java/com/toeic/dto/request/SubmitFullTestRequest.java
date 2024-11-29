package com.toeic.dto.request;

import java.util.List;

import com.toeic.entity.ETestMode;

import lombok.Data;

@Data
public class SubmitFullTestRequest {

	private String email;
	private long testId;
	private ETestMode testMode;
	private int completionTime;
	private List<SubmitAnswer> userAnswers;
}
