package com.toeic.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.toeic.entity.ListeningScoreConversion;

@Repository
public interface ListeningScoreConversionRepository extends JpaRepository<ListeningScoreConversion, Long>{

	Optional<ListeningScoreConversion> findByCorrect(int correct);
}
