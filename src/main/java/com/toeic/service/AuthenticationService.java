package com.toeic.service;

import com.toeic.dto.request.LoginRequest;
import com.toeic.dto.request.RegisterRequest;
import com.toeic.dto.response.LoginResponse;
import com.toeic.entity.User;

import jakarta.mail.MessagingException;

public interface AuthenticationService {
	User register(RegisterRequest registerRequest) throws MessagingException;
	void activateAccount(String activationToken);
	LoginResponse login(LoginRequest loginRequest);
}
