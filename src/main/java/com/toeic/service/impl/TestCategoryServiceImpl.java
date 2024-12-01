package com.toeic.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.toeic.dto.response.TestCategoryDTO;
import com.toeic.entity.TestCategory;
import com.toeic.repository.TestCategoryRepository;
import com.toeic.service.TestCategoryService;
import com.toeic.utils.DTOMapperUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TestCategoryServiceImpl implements TestCategoryService{

	private final TestCategoryRepository testCategoryRepository;

	@Override
	public List<TestCategoryDTO> getAllTestCategory() {
		List<TestCategory> testCategories = testCategoryRepository.findAll();
		return testCategories
				.stream()
				.map(DTOMapperUtils::mapToTestCategoryDTO)
				.collect(Collectors.toList());
	}
	
}
