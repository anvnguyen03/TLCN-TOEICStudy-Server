package com.toeic.entity;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "part")
@NoArgsConstructor
public class Part {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	@Min(1)
	@Max(7)
	private int part_num;
	
	private String content;
	
	private float start_timestamp;
	
	@ManyToOne
	@JoinColumn(name = "test_id", nullable = false)
	private Test test;
	
	@OneToMany(mappedBy = "part", cascade = CascadeType.ALL)
    private List<QuestionGroup> questionGroups;
	
	@OneToMany(mappedBy = "part", cascade = CascadeType.ALL)
    private List<Question> questions;
}
