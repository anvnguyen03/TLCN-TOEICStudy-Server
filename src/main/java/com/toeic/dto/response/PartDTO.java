package com.toeic.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PartDTO {

	private long id;
	private int partNum;
	private String content;
	private float startTimestamp;
	
}
