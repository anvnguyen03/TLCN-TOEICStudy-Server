package com.toeic.entity;

import java.util.List;

import jakarta.persistence.CascadeType;
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
@Table(name = "test")
@Data
@NoArgsConstructor
public class Test {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	private String title;
	
	private int total_questions;
	
	private int duration;	// in minutes
	
	private String listening_audio;
	
	@Enumerated(EnumType.STRING)
	private ETestStatus status = ETestStatus.PUBLISHED;
	
	@ManyToOne
	@JoinColumn(name = "test_category_id")
	private TestCategory testCategory;
	
	@OneToMany(mappedBy = "test", cascade = CascadeType.ALL)
    private List<Part> parts;
	
	@OneToMany(mappedBy = "test", cascade = CascadeType.ALL)
    private List<Question> questions;
	
	@OneToMany(mappedBy = "test", cascade = CascadeType.ALL)
    private List<UserResult> userResults;

}
