package com.toeic.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.toeic.entity.UserResult;

@Repository
public interface UserResultRepository extends JpaRepository<UserResult, Long>{
	boolean existsByTestIdAndUserId(long testId, long userId);
	List<UserResult> findByTestIdAndUserId(long testId, long userId);
}
