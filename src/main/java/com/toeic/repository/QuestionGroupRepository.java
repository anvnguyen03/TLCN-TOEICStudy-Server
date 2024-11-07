package com.toeic.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.toeic.entity.Part;
import com.toeic.entity.QuestionGroup;
import com.toeic.entity.Test;

@Repository
public interface QuestionGroupRepository extends JpaRepository<QuestionGroup, Long>{

	Optional<QuestionGroup> findByNameAndTest(String name, Test test);
	Optional<QuestionGroup> findByNameAndPart(String name, Part part);
	
}
