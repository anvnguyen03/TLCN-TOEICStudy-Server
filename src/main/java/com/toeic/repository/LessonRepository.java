package com.toeic.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.toeic.entity.Lesson;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, Long> {
    List<Lesson> findBySectionId(Long sectionId);
}
