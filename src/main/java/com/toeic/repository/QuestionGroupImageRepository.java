package com.toeic.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.toeic.entity.QuestionGroupImage;

@Repository
public interface QuestionGroupImageRepository extends JpaRepository<QuestionGroupImage, Long>{

	List<QuestionGroupImage> findByQuestionGroupId(long questionGroupId);
}
