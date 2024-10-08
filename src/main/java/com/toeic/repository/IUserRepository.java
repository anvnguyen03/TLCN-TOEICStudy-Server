package com.toeic.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.toeic.entity.ERole;
import com.toeic.entity.User;

@Repository
public interface IUserRepository extends JpaRepository<User, Long>{
	Optional<User> findByEmail(String email);
	boolean existsByEmail(String email);
	Optional<User> findByRole(ERole role);
	Optional<User> findByFullname(String fullname);
}
