package com.toeic.dto.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResultHistoryByTest {
	private String testTitle;
	private List<UserResultDTO> userResults;
}
