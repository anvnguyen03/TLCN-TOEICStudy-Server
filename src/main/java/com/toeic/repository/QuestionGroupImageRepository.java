package com.toeic.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.toeic.entity.QuestionGroupImage;

@Repository
public interface QuestionGroupImageRepository extends JpaRepository<QuestionGroupImage, Long>{

}
