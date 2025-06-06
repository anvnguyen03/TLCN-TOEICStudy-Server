package com.toeic.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.toeic.entity.LessonCompletion;

@Repository
public interface LessonCompletionRepository extends JpaRepository<LessonCompletion, Long> {

    // check if user has completed the lesson
    boolean existsByUserIdAndLessonId(Long userId, Long lessonId);

}
