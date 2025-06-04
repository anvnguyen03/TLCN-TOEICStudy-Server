package com.toeic.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.toeic.entity.EGender;
import com.toeic.entity.ERole;

import lombok.Data;

@Data
public class UserDTO {

	private long id;
	private String fullname;
	private String email;
	private ERole role;
	private boolean activated;
	
	// Personal Information
	private String phone;
	private String address;
	
	@JsonFormat(pattern = "dd/MM/yyyy")
	private LocalDate dateOfBirth;
	private EGender gender;
	private String avatar;
	
	// Education Information
	private String education;
	private String occupation;
	private String englishLevel;
	private Integer targetScore;
	
	// Account Information
	@JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
	private LocalDateTime lastLogin;
	
	@JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
	private LocalDateTime createdAt;
	
	@JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
	private LocalDateTime updatedAt;
}
