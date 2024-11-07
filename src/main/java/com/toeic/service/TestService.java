package com.toeic.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.toeic.dto.response.DisplayTestItemDTO;
import com.toeic.dto.response.TestInfoDTO;

public interface TestService {

	void uploadTest(MultipartFile file, List<MultipartFile> images, List<MultipartFile> audios);
	TestInfoDTO getTestInfo(long testId);
	List<TestInfoDTO> getAllTestInfo();
	List<TestInfoDTO> getPublishedTestsInfo();
	List<DisplayTestItemDTO> getDisplayTestItem(long testId);

}
