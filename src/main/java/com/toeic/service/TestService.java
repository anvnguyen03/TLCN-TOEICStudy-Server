package com.toeic.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.toeic.dto.response.DisplayTestItemDTO;
import com.toeic.dto.response.TestInfoDTO;
import com.toeic.dto.response.TestInfoPagingDTO;
import com.toeic.dto.response.UserResultDTO;
import com.toeic.entity.Test;
import com.toeic.entity.User;

public interface TestService {

	Test uploadFullTest(MultipartFile file, List<MultipartFile> images, List<MultipartFile> audios);
	List<TestInfoDTO> getAllTestInfo();
	TestInfoPagingDTO getByPublishedStatusAndKeywordWithPagination(String keyword, Long testCategoryId, int page, int size, User user);
	TestInfoDTO getTestInfo(long testId);
	TestInfoDTO getTestInfoForLoggedInUser(User user, long testId);
	UserResultDTO getUserResult(User user, long resultId);
	List<UserResultDTO> getUserResultsForUser(User user, long testId);
	List<DisplayTestItemDTO> getDisplayTestItem(long testId);
	
	void deleteTest(long testId);
}
