package com.toeic.service.impl;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.toeic.dto.response.ResultHistoryByTest;
import com.toeic.dto.response.UserDTO;
import com.toeic.dto.response.UserDetailDTO;
import com.toeic.dto.response.UserResultDTO;
import com.toeic.entity.ERole;
import com.toeic.entity.User;
import com.toeic.entity.UserResult;
import com.toeic.repository.UserRepository;
import com.toeic.repository.UserResultRepository;
import com.toeic.service.UserService;
import com.toeic.utils.DTOMapperUtils;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import com.toeic.exception.ResourceNotFoundException;
import com.toeic.dto.request.AdminUpdateUserRequest;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;

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
		List<UserResultDTO> resultsDTO = userResults.stream().map(DTOMapperUtils::mapToUserResultDTO)
				.collect(Collectors.toList());
		// nhóm các resultDTO theo test title
		Map<String, List<UserResultDTO>> groupedByTest = resultsDTO.stream()
				.collect(Collectors.groupingBy(UserResultDTO::getTestTitle));

		return groupedByTest.entrySet().stream().map(entry -> new ResultHistoryByTest(entry.getKey(), entry.getValue()))
				.collect(Collectors.toList());
	}

	@Override
	@Transactional
	public UserDTO updateUser(AdminUpdateUserRequest request) {
		User user = userRepository.findById(request.getUserId())
				.orElseThrow(() -> new ResourceNotFoundException("User not found"));

		// Check if email is already taken by another user
		if (!user.getEmail().equals(request.getEmail()) && 
			userRepository.existsByEmail(request.getEmail())) {
			throw new RuntimeException("Email is already taken");
		}

		// Update basic information
		user.setFullname(request.getFullname());
		user.setEmail(request.getEmail());
		user.setRole(request.getRole());
		user.setActivated(request.isActivated());
		
		// Update personal information
		user.setPhone(request.getPhone());
		user.setAddress(request.getAddress());
		user.setDateOfBirth(request.getDateOfBirth());
		user.setGender(request.getGender());
		user.setAvatar(request.getAvatar());
		
		// Update education information
		user.setEducation(request.getEducation());
		user.setOccupation(request.getOccupation());
		user.setEnglishLevel(request.getEnglishLevel());
		user.setTargetScore(request.getTargetScore());
		
		// Update timestamp
		user.setUpdatedAt(LocalDateTime.now());

		User updatedUser = userRepository.save(user);
		return DTOMapperUtils.mapToUserDTO(updatedUser);
	}

	@Override
	@Transactional
	public void deleteUser(long userId) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User not found"));

		// Check if user has any test results
		if (!userResultRepository.findByUserId(userId).isEmpty()) {
			throw new RuntimeException("Cannot delete user with test history");
		}

		userRepository.delete(user);
	}

	@Override
	public List<UserDTO> searchUsers(String keyword, ERole role, Boolean isActivated) {
		List<User> users = userRepository.searchUsers(keyword, role, isActivated);
		return users.stream()
				.map(DTOMapperUtils::mapToUserDTO)
				.collect(Collectors.toList());
	}

	@Override
	public Page<UserDTO> getAllUsersWithPagination(int page, int size, String sortBy) {
		Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
		Page<User> users = userRepository.findAll(pageable);
		return users.map(DTOMapperUtils::mapToUserDTO);
	}

	@Override
	@Transactional
	public UserDTO toggleUserStatus(long userId, boolean isActivated) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User not found"));
		user.setActivated(isActivated);
		User updatedUser = userRepository.save(user);
		return DTOMapperUtils.mapToUserDTO(updatedUser);
	}

	@Override
	public UserDetailDTO getUserDetail(long userId) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User not found"));
		
		List<UserResult> userResults = userResultRepository.findByUserId(userId);
		List<UserResultDTO> testHistory = userResults.stream()
				.map(DTOMapperUtils::mapToUserResultDTO)
				.collect(Collectors.toList());

		UserDetailDTO userDetail = new UserDetailDTO();
		userDetail.setId(user.getId());
		userDetail.setFullname(user.getFullname());
		userDetail.setEmail(user.getEmail());
		userDetail.setRole(user.getRole());
		userDetail.setActivated(user.isActivated());
		userDetail.setTestHistory(testHistory);
		userDetail.setTotalTestsTaken(testHistory.size());
		
		if (!userResults.isEmpty()) {
			double averageScore = userResults.stream()
					.mapToDouble(result -> result.getTotal_score())
					.average()
					.orElse(0.0);
			userDetail.setAverageScore(averageScore);
		}

		return userDetail;
	}

	@Override
	public Resource exportUsers(String format) {
		List<User> users = userRepository.findAll();
		List<UserDTO> userDTOs = users.stream()
				.map(DTOMapperUtils::mapToUserDTO)
				.collect(Collectors.toList());

		try (Workbook workbook = new XSSFWorkbook()) {
			Sheet sheet = workbook.createSheet("Users");
			
			// Create header row
			Row headerRow = sheet.createRow(0);
			headerRow.createCell(0).setCellValue("ID");
			headerRow.createCell(1).setCellValue("Fullname");
			headerRow.createCell(2).setCellValue("Email");
			headerRow.createCell(3).setCellValue("Role");
			headerRow.createCell(4).setCellValue("Activated");

			// Fill data
			int rowNum = 1;
			for (UserDTO user : userDTOs) {
				Row row = sheet.createRow(rowNum++);
				row.createCell(0).setCellValue(user.getId());
				row.createCell(1).setCellValue(user.getFullname());
				row.createCell(2).setCellValue(user.getEmail());
				row.createCell(3).setCellValue(user.getRole().toString());
				row.createCell(4).setCellValue(user.isActivated());
			}

			// Auto-size columns
			for (int i = 0; i < 5; i++) {
				sheet.autoSizeColumn(i);
			}

			// Write to file
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			workbook.write(outputStream);
			return new ByteArrayResource(outputStream.toByteArray());
		} catch (IOException e) {
			throw new RuntimeException("Error exporting users to Excel", e);
		}
	}

}
