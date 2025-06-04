package com.toeic.dto.response;

import java.util.List;

import com.toeic.entity.ERole;

import lombok.Data;

@Data
public class UserDetailDTO {
    private long id;
    private String fullname;
    private String email;
    private ERole role;
    private boolean activated;
    private List<UserResultDTO> testHistory;
    private int totalTestsTaken;
    private double averageScore;
} 