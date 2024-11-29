package com.toeic.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.toeic.entity.UserResult;

@Repository
public interface UserResultRepository extends JpaRepository<UserResult, Long>{

}