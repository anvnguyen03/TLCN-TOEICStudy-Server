package com.toeic.repository;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.toeic.entity.Comment;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long>{
	List<Comment> findByTestIdAndParentIsNull(long testId, Sort sort);
	List<Comment> findByTestId(long testId);
}
