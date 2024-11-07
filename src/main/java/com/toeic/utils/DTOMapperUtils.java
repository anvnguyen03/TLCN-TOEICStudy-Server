package com.toeic.utils;

import java.util.List;
import java.util.stream.Collectors;

import com.toeic.dto.response.PartDTO;
import com.toeic.dto.response.QuestionDTO;
import com.toeic.dto.response.QuestionGroupDTO;
import com.toeic.dto.response.QuestionGroupImageDTO;
import com.toeic.dto.response.TestInfoDTO;
import com.toeic.entity.Part;
import com.toeic.entity.Question;
import com.toeic.entity.QuestionGroup;
import com.toeic.entity.QuestionGroupImage;
import com.toeic.entity.Test;

public class DTOMapperUtils {
	
	public static TestInfoDTO mapToTestInfoDTO(Test test) {
		
		TestInfoDTO testInfoDTO = new TestInfoDTO();
		testInfoDTO.setId(test.getId());
		testInfoDTO.setTitle(test.getTitle());
		testInfoDTO.setTotalQuestions(test.getTotal_questions());
		testInfoDTO.setDuration(test.getDuration());
		testInfoDTO.setStatus(test.getStatus());
		testInfoDTO.setListeningAudio(test.getListening_audio());
		return testInfoDTO;
		
	}
	
	public static PartDTO mapToPartDTO(Part part) {
		
		PartDTO partDTO = new PartDTO();
		partDTO.setId(part.getId());
		partDTO.setPartNum(part.getPart_num());
		partDTO.setContent(part.getContent());
		partDTO.setStartTimestamp(part.getStart_timestamp());
		return partDTO;
		
	}
	
	public static QuestionGroupDTO mapToQuestionGroupDTO(QuestionGroup questionGroup) {
		
		QuestionGroupDTO questionGroupDTO = new QuestionGroupDTO();
		questionGroupDTO.setId(questionGroup.getId());
		questionGroupDTO.setPartNum(questionGroup.getPart().getPart_num());
		questionGroupDTO.setName(questionGroup.getName());
		questionGroupDTO.setContent(questionGroup.getContent());
		questionGroupDTO.setAudio(questionGroup.getAudio());
		questionGroupDTO.setStartTimestamp(questionGroup.getStart_timestamp());
		
		List<QuestionGroupImageDTO> imagesDTOs = questionGroup.getQuestionGroupImages().stream()
														.map(DTOMapperUtils::mapToQuestionGroupImageDTO)
														.collect(Collectors.toList());
		questionGroupDTO.setImages(imagesDTOs);
		
		List<QuestionDTO> questionsDTO = questionGroup.getQuestions().stream() 
													.map(DTOMapperUtils::mapToQuestionDTO)
													.collect(Collectors.toList());
		questionGroupDTO.setSubQuestions(questionsDTO);
		return questionGroupDTO;
	
	}
	
	public static QuestionGroupImageDTO mapToQuestionGroupImageDTO(QuestionGroupImage image) {
		
		QuestionGroupImageDTO imageDTO = new QuestionGroupImageDTO();
		imageDTO.setId(image.getId());
		imageDTO.setImage(image.getImage());
		return imageDTO;
		
	}
	
	public static QuestionDTO mapToQuestionDTO(Question question) {
		
		QuestionDTO questionDTO = new QuestionDTO();
		questionDTO.setId(question.getId());
		questionDTO.setPartNum(question.getPart().getPart_num());
		questionDTO.setOrderNumber(question.getOrder_number());
		questionDTO.setContent(question.getContent());
		questionDTO.setAnswer1(question.getAnswer_1());
		questionDTO.setAnswer2(question.getAnswer_2());
		questionDTO.setAnswer3(question.getAnswer_3());
		questionDTO.setAnswer4(question.getAnswer_4());
		questionDTO.setImage(question.getImage());
		questionDTO.setAudio(question.getAudio());
		questionDTO.setStartTimestamp(question.getStart_timestamp());
		return questionDTO;
		
	}
	
}