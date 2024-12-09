package com.toeic.service.impl;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.toeic.dto.response.ResultHistoryByTest;
import com.toeic.dto.response.UserDTO;
import com.toeic.dto.response.UserResultDTO;
import com.toeic.entity.User;
import com.toeic.entity.UserResult;
import com.toeic.repository.UserRepository;
import com.toeic.repository.UserResultRepository;
import com.toeic.service.UserService;
import com.toeic.utils.DTOMapperUtils;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;
	private final UserResultRepository userResultRepository;

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

	@Override
	public List<UserDTO> getAllUsers() {
		List<User> users = userRepository.findAll();
		return users.stream().map(DTOMapperUtils::mapToUserDTO).collect(Collectors.toList());
	}

	@Override
	public List<ResultHistoryByTest> getTestHistory(User user) {
		List<UserResult> userResults = userResultRepository.findByUserId(user.getId());
		List<UserResultDTO> resultsDTO = userResults.stream()
													.map(DTOMapperUtils::mapToUserResultDTO)
													.collect(Collectors.toList());
		// nhóm các resultDTO theo test title 
		Map<String, List<UserResultDTO>> groupedByTest = resultsDTO.stream()
																.collect(Collectors.groupingBy(UserResultDTO::getTestTitle));
		
		
		return groupedByTest.entrySet().stream()
				.map(entry -> new ResultHistoryByTest(entry.getKey(), entry.getValue()))
				.collect(Collectors.toList());
	}

}
