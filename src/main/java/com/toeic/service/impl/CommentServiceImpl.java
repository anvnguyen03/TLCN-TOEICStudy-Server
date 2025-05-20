package com.toeic.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.toeic.dto.request.CommentRequest;
import com.toeic.dto.response.CommentDTO;
import com.toeic.entity.Comment;
import com.toeic.entity.Test;
import com.toeic.entity.User;
import com.toeic.exception.ResourceNotFoundException;
import com.toeic.exception.UserNotFoundException;
import com.toeic.repository.CommentRepository;
import com.toeic.repository.TestRepository;
import com.toeic.repository.UserRepository;
import com.toeic.service.CommentService;
import com.toeic.utils.DTOMapperUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService{

	private final TestRepository testRepository;
	private final UserRepository userRepository;
	private final CommentRepository commentRepository;
	
	@Override
	public List<CommentDTO> getCommentsByTest(long testId) {
		Test test = testRepository.findById(testId).orElseThrow(() -> new ResourceNotFoundException("Unknown Test"));
		Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
		List<Comment> comments = commentRepository.findByTestIdAndParentIsNull(testId, sort);
		List<CommentDTO> commentsDTO = comments.stream()
											.map(DTOMapperUtils::mapToCommentDTO)
											.collect(Collectors.toList());
		return commentsDTO;
	}

	@Override
	@Transactional
	public CommentDTO addComment(CommentRequest commentRequest) {
		User user = userRepository.findById(commentRequest.getUserId()).orElseThrow(() -> new UserNotFoundException("Unknown User"));
		Test test = testRepository.findById(commentRequest.getTestId()).orElseThrow(() -> new ResourceNotFoundException("Unknown Test"));
		
		Comment newComment = new Comment();
		newComment.setContent(commentRequest.getContent());
		newComment.setUser(user);
		newComment.setTest(test);
		
		if (commentRequest.getParentId() != null) {
			Comment parentComment = commentRepository.findById(commentRequest.getParentId())
												.orElseThrow(() -> new ResourceNotFoundException("Unknown parent comment"));
			
			parentComment.addChild(newComment);
			// CascadeType.ALL -> newComment sẽ tự động lưu
			commentRepository.save(parentComment);
		} else {
			commentRepository.save(newComment);
		}
		
		return DTOMapperUtils.mapToCommentDTO(newComment);
	}

	@Override
	public void deleteComment(Long id) {
		Comment comment = commentRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Unknown comment"));
		commentRepository.delete(comment);
	}
	
	
}
