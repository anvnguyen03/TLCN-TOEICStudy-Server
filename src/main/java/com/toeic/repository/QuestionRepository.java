package com.toeic.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.toeic.entity.Question;
import com.toeic.entity.Test;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long>{

	@Query("SELECT q FROM Question q WHERE q.order_number = :orderNumber AND q.test = :test")
	Optional<Question> findByOrderNumberAndTest(@Param("orderNumber") int orderNumber, @Param("test") Test test);
	 
}
