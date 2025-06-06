package com.toeic.repository;

import com.toeic.entity.AITestAnalysis;
import com.toeic.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AITestAnalysisRepository extends JpaRepository<AITestAnalysis, Long> {
    List<AITestAnalysis> findByUserOrderByCreatedAtDesc(User user);
    long countByUser(User user);
} 