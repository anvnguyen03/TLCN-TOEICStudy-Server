package com.toeic.dto.response;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class QuestionGroupDTO {

	private long id;
	private int partNum;
	private String name;
	private String content;
	private String audio;
	private float startTimestamp;
	private List<QuestionGroupImageDTO> images;
	private List<QuestionDTO> subQuestions;

}
