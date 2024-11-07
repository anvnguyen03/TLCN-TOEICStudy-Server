package com.toeic.dto.response;

import com.toeic.entity.ETestStatus;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TestInfoDTO {

	private long id;
	private String title;
	private int totalQuestions;
	private int duration;
	private String listeningAudio;
	private ETestStatus status;
	
}
