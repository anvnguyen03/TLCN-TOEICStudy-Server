package com.toeic.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.toeic.entity.ETestStatus;
import com.toeic.entity.Test;

@Repository
public interface TestRepository extends JpaRepository<Test, Long>{
	// Phân trang và tìm kiếm title với status
	Page<Test> findByStatusAndTitleContainingIgnoreCase(ETestStatus status, String keyword, Pageable pageable);
}
