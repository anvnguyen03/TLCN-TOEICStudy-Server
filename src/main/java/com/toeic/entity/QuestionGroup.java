package com.toeic.entity;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "question_group")
@Data
@NoArgsConstructor
public class QuestionGroup {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	private String name;
	
	@Lob
	private String content;
	
	@OneToMany(mappedBy = "questionGroup", cascade = CascadeType.ALL)
	private List<QuestionGroupImage> questionGroupImages;
	
	private String audio;
	
	private float start_timestamp;
	
	@ManyToOne
	@JoinColumn(name = "part_id")
	private Part part;
	
	@ManyToOne
	@JoinColumn(name = "test_id")
	private Test test;
	
	@OneToMany(mappedBy = "questionGroup", cascade = CascadeType.ALL)
	private List<Question> questions;
}