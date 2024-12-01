package com.toeic.dto.response;

import java.util.List;

import lombok.Data;

@Data
public class TestInfoPagingDTO {

	private List<TestInfoDTO> tests;
	private int totalPages;
	private long totalElements;
	private int currentPageIndex;
	private int numberOfElements;
	
}
