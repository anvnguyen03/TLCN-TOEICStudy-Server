package com.toeic.service.impl;

import java.util.HashMap;
import java.util.regex.Pattern;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.toeic.dto.request.LoginRequest;
import com.toeic.dto.request.RegisterRequest;
import com.toeic.dto.response.LoginResponse;
import com.toeic.entity.User;
import com.toeic.exception.InvalidCredentialsException;
import com.toeic.exception.InvalidEmailException;
import com.toeic.exception.InvalidPasswordException;
import com.toeic.exception.InvalidTokenException;
import com.toeic.exception.UserAlreadyExistsException;
import com.toeic.repository.UserRepository;
import com.toeic.service.AuthenticationService;
import com.toeic.service.EmailService;
import com.toeic.service.JWTService;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JWTService jwtService;
	private final EmailService emailService;
	private final AuthenticationManager authenticationManager;

	private static final String EMAIL_PATTERN = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
	// regex for minimum 6 characters, at least 1 letter and 1 number
	private static final String PASSWORD_PATTERN = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{6,}$";
	private Pattern pattern;

	@Override
	public User register(RegisterRequest registerRequest) throws MessagingException {

		// check if the user already exists
		if (userRepository.existsByEmail(registerRequest.getEmail())) {
			throw new UserAlreadyExistsException("User already existed, please use another email address.");
		}

		// validate the email
		if (!isValidEmail(registerRequest.getEmail())) {
			throw new InvalidEmailException("Invalid email format. Please provide a valid email address");
		}

		// validate the password
		if (!isValidPassword(registerRequest.getPassword())) {
			throw new InvalidPasswordException(
					"Password must be at least 6 characters long and include both letters and numbers.");
		}

		User user = new User();
		user.setEmail(registerRequest.getEmail());
		user.setFullname(registerRequest.getFullname());
		user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));

		try {
			final String activationToken = jwtService.generateActiveToken(user);
			final String siteURL = "http://localhost:5173/register/verify/" + activationToken;
			final String subject = "TOEIC Study - Account activation";
			final String body = "<p>To complete the registration process, please click on the link below to activate your account</p>"
					+ "<a href=\"" + siteURL + "\">Active now!</a>";

			emailService.sendEmail(user.getEmail(), subject, body);
			User createdUser = userRepository.save(user);
			return createdUser;
		} catch (MessagingException e) {
			throw e;
		}
	}

	private boolean isValidEmail(String email) {
		pattern = Pattern.compile(EMAIL_PATTERN);
		return pattern.matcher(email).matches();
	}

	private boolean isValidPassword(String password) {
		pattern = Pattern.compile(PASSWORD_PATTERN);
		return pattern.matcher(password).matches();
	}

	@Override
	public void activateAccount(String activationToken) {
		if (!jwtService.isTokenValid(activationToken)) {
			throw new InvalidTokenException("Invalid or expired token. Please use another one");
		}

		User currentUser = userRepository.findByEmail(jwtService.extractUsername(activationToken)).get();
		currentUser.setActivated(true);
		userRepository.save(currentUser);
		// next: disable token with blacklist
	}

	@Override
	public LoginResponse login(LoginRequest loginRequest) {
		
		// khởi tạo một đối tượng authentication để thực hiện xác thực qua lớp Username Password
		Authentication authentication;

		try {
			authentication = authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
		} catch (AuthenticationException e) {
			authentication = null;
		}

		LoginResponse loginResponse = new LoginResponse();

		// kiểm tra xem đối tượng authentication đã xác thực thành công chưa
		if (authentication != null && authentication.isAuthenticated()) {
			var user = userRepository.findByEmail(loginRequest.getEmail())
					.orElseThrow(() -> new InvalidCredentialsException("Invalid credentails"));
			var jwt = jwtService.generateToken(user);
			var refreshToken = jwtService.generateRefreshToken(new HashMap<>(), user);

			loginResponse.setToken(jwt);
			loginResponse.setRefreshToken(refreshToken);
		} else {
			throw new InvalidCredentialsException("Invalid credentails");
		}

		return loginResponse;
	}
}
