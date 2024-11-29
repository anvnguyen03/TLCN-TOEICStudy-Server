package com.toeic.entity;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

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

	private int correct_answers;
	private int incorrect_answers;
	private int skipped_answers;
	private int listening_corrects;
	private int reading_corrects;
	private int listening_score;
	private int reading_score;
	private int total_score;
	private int completion_time;	// in seconds
	private float accuracy;
	
	@JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
	private LocalDateTime completed_at;
	
	@Enumerated(EnumType.STRING)
	private EAttempStatus attemp_status;
	
	@Enumerated(EnumType.STRING)
	private ETestMode test_mode;
	
	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;
	
	@ManyToOne
	@JoinColumn(name = "test_id")
	private Test test;
	
	@OneToMany(mappedBy = "userResult")
	private List<UserAnswer> userAnswers;
}