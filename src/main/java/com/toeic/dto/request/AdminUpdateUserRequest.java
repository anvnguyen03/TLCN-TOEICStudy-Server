package com.toeic.dto.request;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.toeic.entity.EGender;
import com.toeic.entity.ERole;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class AdminUpdateUserRequest {
    @NotNull(message = "User ID is required")
    private long userId;
    
    @NotBlank(message = "Fullname is required")
    private String fullname;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;
    
    @NotNull(message = "Role is required")
    private ERole role;
    
    private boolean activated;
    
    // Personal Information
    @Pattern(regexp = "^\\d{10}$", message = "Phone number must be 10 digits")
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
} 