package com.toeic.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_answer")
@Data
@NoArgsConstructor
public class UserAnswer {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	private String selected_answer;
	
	private boolean is_correct;
	
	@ManyToOne
	@JoinColumn(name = "user_result_id")
	private UserResult userResult;
	
	@ManyToOne
	@JoinColumn(name = "question_id")
	private Question question;
}