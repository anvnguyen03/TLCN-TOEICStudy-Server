package com.toeic.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.toeic.entity.TestCategory;

@Repository
public interface TestCategoryRepository extends JpaRepository<TestCategory, Long>{
	Optional<TestCategory> findByName(String name);
}
