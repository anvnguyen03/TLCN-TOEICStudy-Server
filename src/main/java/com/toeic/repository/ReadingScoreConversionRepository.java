package com.toeic.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.toeic.entity.ReadingScoreConversion;

@Repository
public interface ReadingScoreConversionRepository extends JpaRepository<ReadingScoreConversion, Long>{

	Optional<ReadingScoreConversion> findByCorrect(int correct);
}
