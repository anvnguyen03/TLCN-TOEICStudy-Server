package com.toeic.entity;

import java.time.LocalDateTime;
import java.util.Set;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_result")
@Data
@NoArgsConstructor
public class UserResult {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	private int total_score;
	
	private LocalDateTime completed_at;
	
	@Enumerated(EnumType.STRING)
	private EAttempStatus attemp_status;
	
	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;
	
	@ManyToOne
	@JoinColumn(name = "test_id")
	private Test test;
	
	@OneToMany(mappedBy = "userResult")
	private Set<UserAnswer> userAnswers;
}