package com.toeic.service;

import com.toeic.dto.request.SubmitFullTestRequest;
import com.toeic.dto.response.UserResultDTO;

public interface ScoringService {

	UserResultDTO submitFullTest(SubmitFullTestRequest submitRequest);
}
