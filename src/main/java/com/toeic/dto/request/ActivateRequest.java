package com.toeic.dto.request;

import lombok.Data;

@Data
public class ActivateRequest {
	String email;
	String otp;
}
