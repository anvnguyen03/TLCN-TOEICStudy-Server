package com.toeic.service;

import java.util.List;
import java.util.Optional;

import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.security.core.userdetails.UserDetailsService;

import com.toeic.dto.request.AdminUpdateUserRequest;
import com.toeic.dto.response.ResultHistoryByTest;
import com.toeic.dto.response.UserDTO;
import com.toeic.dto.response.UserDetailDTO;
import com.toeic.entity.ERole;
import com.toeic.entity.User;

public interface UserService {
	
	List<UserDTO> getAllUsers();
	
	List<ResultHistoryByTest> getTestHistory(User user);

	UserDetailsService userDetailsService();

	Optional<User> findByUserFullname(String username);

	Optional<User> findByUserEmail(String email);

	User update(User currentUser);
	
	boolean isEmailExisted(String email);
	
	List<UserDTO> searchUsers(String keyword, ERole role, Boolean isActivated);
	
	Page<UserDTO> getAllUsersWithPagination(int page, int size, String sortBy);
	
	UserDTO toggleUserStatus(long userId, boolean isActivated);
	
	UserDetailDTO getUserDetail(long userId);
	
	Resource exportUsers(String format);
	
	UserDTO updateUser(AdminUpdateUserRequest request);
	
	void deleteUser(long userId);
	
}
