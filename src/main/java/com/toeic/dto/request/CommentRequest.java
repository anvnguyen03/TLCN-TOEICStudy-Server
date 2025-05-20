package com.toeic.dto.request;

import lombok.Data;

@Data
public class CommentRequest {

	private String content;
	private long userId;
	private long testId;
	private Long parentId;	// may be null
	
}
