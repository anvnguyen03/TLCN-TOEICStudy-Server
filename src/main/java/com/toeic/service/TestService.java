package com.toeic.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.toeic.dto.response.DisplayTestItemDTO;
import com.toeic.dto.response.TestInfoDTO;
import com.toeic.dto.response.UserResultDTO;
import com.toeic.entity.User;

public interface TestService {

	void uploadTest(MultipartFile file, List<MultipartFile> images, List<MultipartFile> audios, String categoryName);
	TestInfoDTO getTestInfo(long testId);
	List<TestInfoDTO> getAllTestInfo();
	List<TestInfoDTO> getPublishedTestsInfo();
	List<DisplayTestItemDTO> getDisplayTestItem(long testId);
	UserResultDTO getUserResult(User user, long resultId);

}
