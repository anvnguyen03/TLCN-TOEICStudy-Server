package com.toeic.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.toeic.entity.ETestStatus;

import lombok.Data;

@Data
public class TestInfoDTO {

	private long id;
	private String title;
	private int duration;
	private int totalAttemps;
	private int totalComments;
	private int totalParts;
	private int totalQuestions;
	private String testCategory;
	private String listeningAudio;
	private ETestStatus status;
	
	@JsonProperty("isUserAttemped")
	private boolean isUserAttemped;
}
