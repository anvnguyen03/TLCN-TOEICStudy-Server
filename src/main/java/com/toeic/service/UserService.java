package com.toeic.service;

import java.util.List;
import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.toeic.dto.response.ResultHistoryByTest;
import com.toeic.dto.response.UserDTO;
import com.toeic.entity.User;

public interface UserService {
	
	List<UserDTO> getAllUsers();
	
	List<ResultHistoryByTest> getTestHistory(User user);

	UserDetailsService userDetailsService();

	Optional<User> findByUserFullname(String username);

	Optional<User> findByUserEmail(String email);

	User update(User currentUser);
	
	boolean isEmailExisted(String email);
	
}
