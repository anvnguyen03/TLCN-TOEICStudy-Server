package com.toeic.dto.request;

import lombok.Data;

@Data
public class ResetPassRequest {

	private String email;
	private String otp;
	private String newPassword;
}
