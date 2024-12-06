package com.toeic.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.toeic.entity.Part;
import com.toeic.entity.Test;

@Repository
public interface PartRepository extends JpaRepository<Part, Long>{
	
	@Query("SELECT p FROM Part p WHERE p.part_num = :partNum AND p.test = :test")
	Optional<Part> findByPartNumAndTest(@Param("partNum") int partNum, @Param("test") Test test);
	List<Part> findByTestId(long testId);
}
