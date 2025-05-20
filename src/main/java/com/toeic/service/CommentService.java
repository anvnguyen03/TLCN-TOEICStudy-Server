package com.toeic.service;

import java.util.List;

import com.toeic.dto.request.CommentRequest;
import com.toeic.dto.response.CommentDTO;

public interface CommentService {
	List<CommentDTO> getCommentsByTest(long testId);
	CommentDTO addComment(CommentRequest commentRequest);
	void deleteComment(Long id);
}
