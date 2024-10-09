package com.toeic.service.impl;

import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.toeic.entity.User;
import com.toeic.repository.UserRepository;
import com.toeic.service.UserService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{

private final UserRepository userRepository;
	
	@Override
	public UserDetailsService userDetailsService() {
		return new UserDetailsService() {
			
			@Override
			public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
				return userRepository.findByEmail(username)
						.orElseThrow(() -> new UsernameNotFoundException("User not found"));
			}
		};
	}

	@Override
	public Optional<User> findByUserFullname(String fullname) {
		return userRepository.findByFullname(fullname);
	}

	@Override
	public Optional<User> findByUserEmail(String email) {
		return userRepository.findByEmail(email);
	}

	@Override
	public User update(User currentUser) {
		if (!userRepository.existsById(currentUser.getId())) {
	        throw new EntityNotFoundException("User not found");
	    }
	    return userRepository.save(currentUser);
	}

	@Override
	public boolean isEmailExisted(String email) {
		return userRepository.existsByEmail(email);
	}
	
}
