package com.toeic.dto.request;

import com.toeic.entity.ERole;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
} 