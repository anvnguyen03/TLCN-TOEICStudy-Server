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
@Table(name = "question_group_image")
@Data
@NoArgsConstructor
public class QuestionGroupImage {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	private String image;
	
	@ManyToOne
	@JoinColumn(name = "question_group_id")
	private QuestionGroup questionGroup;
}
