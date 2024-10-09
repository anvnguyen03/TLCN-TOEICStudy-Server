package com.toeic.service;

import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.toeic.entity.User;

public interface UserService {

	UserDetailsService userDetailsService();

	Optional<User> findByUserFullname(String username);

	Optional<User> findByUserEmail(String email);

	User update(User currentUser);
	
	boolean isEmailExisted(String email);
	
}
