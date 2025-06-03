package com.toeic.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.toeic.entity.ERole;
import com.toeic.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long>{
	Optional<User> findByEmail(String email);
	boolean existsByEmail(String email);
	Optional<User> findByRole(ERole role);
	Optional<User> findByFullname(String fullname);

	@Query("SELECT u FROM User u WHERE " +
		   "(:keyword IS NULL OR LOWER(u.fullname) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
		   "(:role IS NULL OR u.role = :role) AND " +
		   "(:isActivated IS NULL OR u.isActivated = :isActivated)")
	List<User> searchUsers(@Param("keyword") String keyword, 
						  @Param("role") ERole role, 
						  @Param("isActivated") Boolean isActivated);
}
