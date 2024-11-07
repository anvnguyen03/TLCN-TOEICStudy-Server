package com.toeic.entity;

import java.util.Set;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "question")
@Data
@NoArgsConstructor
public class Question {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	private int order_number;
	
	private String content;
	
	private String answer_1;
	
	private String answer_2;
	
	private String answer_3;
	
	private String answer_4;
	
	private String correct_answer;
	
	@Lob
	private String transcript;
	
	private String image;
	
	private String audio;
	
	private float start_timestamp;
	
	@ManyToOne
	@JoinColumn(name = "part_id")
	private Part part;
	
	@ManyToOne
	@JoinColumn(name = "question_group_id", nullable = true)
	private QuestionGroup questionGroup;
	
	@ManyToOne
	@JoinColumn(name = "test_id")
	private Test test;
	
	@ManyToMany
    @JoinTable(
        name = "question_category_mapping",
        joinColumns = @JoinColumn(name = "question_id"),
        inverseJoinColumns = @JoinColumn(name = "question_category_id")
    )
    private Set<QuestionCategory> questionCategories;
}
