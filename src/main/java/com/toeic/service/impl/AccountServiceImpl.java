package com.toeic.service.impl;

import org.springframework.stereotype.Service;

import com.toeic.entity.User;
import com.toeic.exception.InvalidCredentialsException;
import com.toeic.exception.UserNotFoundException;
import com.toeic.repository.UserRepository;
import com.toeic.service.AccountService;
import com.toeic.service.JWTService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService{
	private final UserRepository userRepository;
	private final JWTService jwtService;
	
	@Override
	public User fetchAccount(String token) {
		String username = jwtService.extractUsername(token);
		
		if (username == null || username == "") {
			throw new InvalidCredentialsException("Invalid credentials");
		}
		
		User user = userRepository.findByEmail(username).orElseThrow(() -> new UserNotFoundException("User not found"));
		return user;
	}
	
}
