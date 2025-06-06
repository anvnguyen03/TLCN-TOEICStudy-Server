package com.toeic.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;

import com.toeic.dto.response.CardMatchingQuestionDTO;
import com.toeic.dto.response.CommentDTO;
import com.toeic.dto.response.CourseCardDTO;
import com.toeic.dto.response.CourseDetailDTO;
import com.toeic.dto.response.CourseInfoDTO;
import com.toeic.dto.response.CourseReviewDTO;
import com.toeic.dto.response.CourseSectionDetailDTO;
import com.toeic.dto.response.CourseSectionPreviewDTO;
import com.toeic.dto.response.LessonDetailDTO;
import com.toeic.dto.response.LessonPreviewDTO;
import com.toeic.dto.response.MatchingAnswerDTO;
import com.toeic.dto.response.MatchingPromptDTO;
import com.toeic.dto.response.PartDTO;
import com.toeic.dto.response.QuestionDTO;
import com.toeic.dto.response.QuestionGroupDTO;
import com.toeic.dto.response.QuestionGroupImageDTO;
import com.toeic.dto.response.QuizQuestionDTO;
import com.toeic.dto.response.QuizQuestionOptionDTO;
import com.toeic.dto.response.ReviewQuestionDTO;
import com.toeic.dto.response.TestCategoryDTO;
import com.toeic.dto.response.TestInfoDTO;
import com.toeic.dto.response.TestInfoPagingDTO;
import com.toeic.dto.response.UserAnswerDTO;
import com.toeic.dto.response.UserDTO;
import com.toeic.dto.response.UserResultDTO;
import com.toeic.entity.CardMatchingPair;
import com.toeic.entity.Comment;
import com.toeic.entity.Course;
import com.toeic.entity.CourseReview;
import com.toeic.entity.CourseSection;
import com.toeic.entity.ELessonType;
import com.toeic.entity.EQuizType;
import com.toeic.entity.Lesson;
import com.toeic.entity.Part;
import com.toeic.entity.Question;
import com.toeic.entity.QuestionGroup;
import com.toeic.entity.QuestionGroupImage;
import com.toeic.entity.QuizQuestion;
import com.toeic.entity.QuizQuestionOption;
import com.toeic.entity.Test;
import com.toeic.entity.TestCategory;
import com.toeic.entity.User;
import com.toeic.entity.UserAnswer;
import com.toeic.entity.UserResult;

public class DTOMapperUtils {
	
	public static UserResultDTO mapToUserResultDTO(UserResult userResult) {
		UserResultDTO userResultDTO = new UserResultDTO();
		userResultDTO.setId(userResult.getId());
		userResultDTO.setCorrectAnswers(userResult.getCorrect_answers());
		userResultDTO.setIncorrectAnswers(userResult.getIncorrect_answers());
		userResultDTO.setSkippedAnswers(userResult.getSkipped_answers());
		userResultDTO.setListeningCorrects(userResult.getListening_corrects());
	    userResultDTO.setReadingCorrects(userResult.getReading_corrects());
	    userResultDTO.setListeningScore(userResult.getListening_score());
	    userResultDTO.setReadingScore(userResult.getReading_score());
	    userResultDTO.setTotalScore(userResult.getTotal_score());
	    userResultDTO.setCompletionTime(userResult.getCompletion_time());
	    userResultDTO.setAccuracy(userResult.getAccuracy());
	    userResultDTO.setCompletedAt(userResult.getCompleted_at());
	    userResultDTO.setAttempStatus(userResult.getAttemp_status().name());
	    userResultDTO.setTestMode(userResult.getTest_mode().name());
	    userResultDTO.setUserId(userResult.getUser().getId());
	    userResultDTO.setTestId(userResult.getTest().getId());
	    userResultDTO.setTestTitle(userResult.getTest().getTitle());
	    
	    List<UserAnswerDTO> userAnswers = userResult.getUserAnswers().stream()
	    									.map(DTOMapperUtils::mapToUserAnswerDTO)
											.collect(Collectors.toList());
	    userResultDTO.setUserAnswers(userAnswers);
	    return userResultDTO;
	}
	
	public static UserAnswerDTO mapToUserAnswerDTO(UserAnswer userAnswer) {
		UserAnswerDTO userAnswerDTO = new UserAnswerDTO();
		userAnswerDTO.setId(userAnswer.getId());
		userAnswerDTO.setUserResultId(0);
		userAnswerDTO.setCorrect(userAnswer.getIsCorrect());
		userAnswerDTO.setSelectedAnswer(userAnswer.getSelected_answer());
		
		ReviewQuestionDTO question = mapToReviewQuestionDTO(userAnswer.getQuestion());
		userAnswerDTO.setQuestion(question);
		return userAnswerDTO;
	}
	
	public static TestInfoDTO mapToTestInfoDTO(Test test) {
		
		TestInfoDTO testInfoDTO = new TestInfoDTO();
		testInfoDTO.setId(test.getId());
		testInfoDTO.setTitle(test.getTitle());
		testInfoDTO.setDuration(test.getDuration());
		
		if (test.getUserResults() != null) {
			testInfoDTO.setTotalAttemps(test.getUserResults().size());
		} else {
			testInfoDTO.setTotalAttemps(0);
		}
		
		testInfoDTO.setTotalComments(0);
		testInfoDTO.setTotalParts(test.getParts().size());
		testInfoDTO.setTotalQuestions(test.getTotal_questions());
		testInfoDTO.setTestCategory(test.getTestCategory().getName());
		testInfoDTO.setListeningAudio(test.getListening_audio());
		testInfoDTO.setStatus(test.getStatus());
		// Tiếp tục kiểm tra xem user hiện tại đã từng làm test này chưa trong logic service
		testInfoDTO.setUserAttemped(false);
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
	
	public static ReviewQuestionDTO mapToReviewQuestionDTO(Question question) {
		
		ReviewQuestionDTO reviewQuestionDTO = new ReviewQuestionDTO();
		reviewQuestionDTO.setId(question.getId());
		reviewQuestionDTO.setPartNum(question.getPart().getPart_num());
		reviewQuestionDTO.setOrderNumber(question.getOrder_number());
		reviewQuestionDTO.setContent(question.getContent());
		reviewQuestionDTO.setAnswer1(question.getAnswer_1());
		reviewQuestionDTO.setAnswer2(question.getAnswer_2());
		reviewQuestionDTO.setAnswer3(question.getAnswer_3());
		reviewQuestionDTO.setAnswer4(question.getAnswer_4());
		if (question.getQuestionGroup() != null) {
			List<String> groupImages = new ArrayList<>();
			for (QuestionGroupImage qGroupImage : question.getQuestionGroup().getQuestionGroupImages()) {
				groupImages.add(qGroupImage.getImage());
			}
			reviewQuestionDTO.setImages(groupImages);
			reviewQuestionDTO.setGroupContent(question.getQuestionGroup().getContent());
		} else {
			if (question.getImage() != null) {
				List<String> image = new ArrayList<>();
				image.add(question.getImage());
				reviewQuestionDTO.setImages(image);
			} else {
				reviewQuestionDTO.setImages(null);
			}
		}
		
		reviewQuestionDTO.setAudio(question.getAudio());
		reviewQuestionDTO.setStartTimestamp(question.getStart_timestamp());
		reviewQuestionDTO.setCorrectAnswer(question.getCorrect_answer());
		reviewQuestionDTO.setTranscript(question.getTranscript());
		return reviewQuestionDTO;
		
	}
	
	public static TestInfoPagingDTO mapToTestInfoPagingDTO(Page<TestInfoDTO> testInfo) {
		TestInfoPagingDTO testInfoPaging = new TestInfoPagingDTO();
		testInfoPaging.setTests(testInfo.getContent());
		testInfoPaging.setTotalPages(testInfo.getTotalPages());
		testInfoPaging.setTotalElements(testInfo.getTotalElements());
		testInfoPaging.setCurrentPageIndex(testInfo.getNumber());
		testInfoPaging.setNumberOfElements(testInfo.getNumberOfElements());
		return testInfoPaging;
	}
	
	public static TestCategoryDTO mapToTestCategoryDTO(TestCategory testCategory) {
		TestCategoryDTO categoryDTO = new TestCategoryDTO();
		categoryDTO.setId(testCategory.getId());
		categoryDTO.setName(testCategory.getName());
		return categoryDTO;
	}
	
	public static UserDTO mapToUserDTO(User user) {
		UserDTO userDTO = new UserDTO();
		userDTO.setId(user.getId());
		userDTO.setFullname(user.getFullname());
		userDTO.setEmail(user.getEmail());
		userDTO.setRole(user.getRole());
		userDTO.setActivated(user.isActivated());
		
		// Map personal information
		userDTO.setPhone(user.getPhone());
		userDTO.setAddress(user.getAddress());
		userDTO.setDateOfBirth(user.getDateOfBirth());
		userDTO.setGender(user.getGender());
		userDTO.setAvatar(user.getAvatar());
		
		// Map education information
		userDTO.setEducation(user.getEducation());
		userDTO.setOccupation(user.getOccupation());
		userDTO.setEnglishLevel(user.getEnglishLevel());
		userDTO.setTargetScore(user.getTargetScore());
		
		// Map account information
		userDTO.setLastLogin(user.getLastLogin());
		userDTO.setCreatedAt(user.getCreatedAt());
		userDTO.setUpdatedAt(user.getUpdatedAt());
		
		return userDTO;
	}
	
	public static CommentDTO mapToCommentDTO(Comment comment) {
		if (comment == null) {
			return null;
		}
		
		CommentDTO commentDTO = new CommentDTO();
		commentDTO.setId(comment.getId());
		commentDTO.setContent(comment.getContent());
		commentDTO.setCreatedAt(comment.getCreatedAt());
		commentDTO.setUpdatedAt(comment.getUpdatedAt());
		commentDTO.setUserId(comment.getUser().getId());
		commentDTO.setUsername(comment.getUser().getFullname());
		commentDTO.setTestId(comment.getTest().getId());
		
		if (comment.getParent() != null) {
			commentDTO.setParentId(comment.getParent().getId());
		}
		
		// Đệ quy Map children
		List<CommentDTO> childrenDTO = new ArrayList<>();
		if (comment.getChildren() != null && !comment.getChildren().isEmpty()) {
			for (Comment child : comment.getChildren()) {
				CommentDTO childDTO = mapToCommentDTO(child);
				childrenDTO.add(childDTO);
			}
		}
		commentDTO.setChildren(childrenDTO);
		return commentDTO;
	}

	public static CourseCardDTO mapToCourseCardDTO(Course course) {
		CourseCardDTO courseCardDTO = new CourseCardDTO();
		courseCardDTO.setId(course.getId());
		courseCardDTO.setTitle(course.getTitle());
		courseCardDTO.setLevel(course.getLevel().name());
		courseCardDTO.setPrice(course.getPrice().doubleValue());
		courseCardDTO.setImage(course.getThumbnail_url());
		
		return courseCardDTO;
	}

	public static CourseInfoDTO mapToCourseInfoDTO(Course course) {
		CourseInfoDTO courseInfoDTO = new CourseInfoDTO();
		courseInfoDTO.setId(course.getId());
		courseInfoDTO.setTitle(course.getTitle());
		courseInfoDTO.setDescription(course.getDescription());
		courseInfoDTO.setObjective(course.getObjective());
		courseInfoDTO.setThumbnailUrl(course.getThumbnail_url());
		courseInfoDTO.setPreviewVideoUrl(course.getPreview_video_url());
		courseInfoDTO.setPrice(course.getPrice().doubleValue());
		courseInfoDTO.setTotalReviews(course.getReviews().size());
		courseInfoDTO.setStudents(course.getEnrollments().size());
		courseInfoDTO.setTotalSections(course.getSections().size());
		courseInfoDTO.setTotalLessons(course.getSections().stream()
				.mapToInt(section -> section.getLessons().size())
				.sum());
				
		// set sections
		List<CourseSectionPreviewDTO> sections = new ArrayList<>();
		for (CourseSection section : course.getSections()) {
			CourseSectionPreviewDTO sectionDTO = mapToCourseSectionPreviewDTO(section);
			sections.add(sectionDTO);
		}
		courseInfoDTO.setSections(sections);
		return courseInfoDTO;
	}

	public static CourseSectionPreviewDTO mapToCourseSectionPreviewDTO(CourseSection section) {
		CourseSectionPreviewDTO sectionDTO = new CourseSectionPreviewDTO();
		sectionDTO.setId(section.getId());
		sectionDTO.setTitle(section.getTitle());
		sectionDTO.setOrderIndex(section.getOrderIndex());
		sectionDTO.setTotalLessons(section.getLessons().size());

		// duration calculate from lessons
		int duration = 0;
		for (Lesson lesson : section.getLessons()) {
			duration += lesson.getDuration();
		}
		sectionDTO.setDuration(duration);

		// set lessons
		List<LessonPreviewDTO> lessons = new ArrayList<>();
		for (Lesson lesson : section.getLessons()) {
			LessonPreviewDTO lessonDTO = mapToLessonPreviewDTO(lesson);
			lessons.add(lessonDTO);
		}
		sectionDTO.setLessons(lessons);
		return sectionDTO;
	}

	public static LessonPreviewDTO mapToLessonPreviewDTO(Lesson lesson) {
		LessonPreviewDTO lessonDTO = new LessonPreviewDTO();
		lessonDTO.setId(lesson.getId());
		lessonDTO.setTitle(lesson.getTitle());
		lessonDTO.setOrderIndex(lesson.getOrderIndex());
		lessonDTO.setDuration(lesson.getDuration());
		lessonDTO.setType(lesson.getType());
		lessonDTO.setFree(lesson.getIsFree());
		return lessonDTO;
	}

	public static CourseReviewDTO mapToCourseReviewDTO(CourseReview review) {
		CourseReviewDTO reviewDTO = new CourseReviewDTO();
		reviewDTO.setId(review.getId());
		reviewDTO.setComment(review.getComment());
		reviewDTO.setRating(review.getRating());
		reviewDTO.setUsername(review.getUser().getFullname());
		reviewDTO.setCreatedAt(review.getCreatedAt());
		return reviewDTO;
	}

	public static LessonDetailDTO mapToLessonDetailDTO(Lesson lesson) {
		LessonDetailDTO lessonDTO = new LessonDetailDTO();
		lessonDTO.setId(lesson.getId());
		lessonDTO.setTitle(lesson.getTitle());
		lessonDTO.setDescription(lesson.getDescription());
		lessonDTO.setOrderIndex(lesson.getOrderIndex());
		lessonDTO.setType(lesson.getType().name());
		lessonDTO.setDuration(lesson.getDuration());
		lessonDTO.setIsFree(lesson.getIsFree());

		if (lesson.getType() == ELessonType.TEXT) {
			lessonDTO.setContent(lesson.getContent());
		} else if (lesson.getType() == ELessonType.VIDEO) {
			lessonDTO.setVideoUrl(lesson.getVideoUrl());
		}

		List<QuizQuestionDTO> quizQuestions = new ArrayList<>();
		for (QuizQuestion quizQuestion : lesson.getQuizQuestions()) {
			QuizQuestionDTO quizQuestionDTO = mapToQuizQuestionDTO(quizQuestion);
			quizQuestions.add(quizQuestionDTO);
		}
		lessonDTO.setQuizQuestions(quizQuestions);

		return lessonDTO;
	}

	public static QuizQuestionDTO mapToQuizQuestionDTO(QuizQuestion quizQuestion) {
		QuizQuestionDTO quizQuestionDTO = new QuizQuestionDTO();
		quizQuestionDTO.setId(quizQuestion.getId());
		quizQuestionDTO.setType(quizQuestion.getType().name());
		quizQuestionDTO.setOrderIndex(quizQuestion.getOrderIndex());
		quizQuestionDTO.setQuestion(quizQuestion.getQuestion());

		if (quizQuestion.getType() == EQuizType.MULTIPLE_CHOICE) {
			quizQuestionDTO.setOption(mapToQuizQuestionOptionDTO(quizQuestion.getOption()));
		} else if (quizQuestion.getType() == EQuizType.CARD_MATCHING) {
			// Get all pairs
			List<CardMatchingPair> pairs = quizQuestion.getPairs();
			// Create prompts and answers in parallel
			List<MatchingPromptDTO> prompts = new ArrayList<>();
			List<MatchingAnswerDTO> answers = new ArrayList<>();
			
			for (CardMatchingPair pair : pairs) {
				MatchingPromptDTO prompt = new MatchingPromptDTO();
				prompt.setId(pair.getId());
				prompt.setContent(pair.getPrompt());
				prompts.add(prompt);

				MatchingAnswerDTO answer = new MatchingAnswerDTO();
				answer.setContent(pair.getAnswer());
				answers.add(answer);
			}

			// Shuffle both lists
			Collections.shuffle(prompts);
			Collections.shuffle(answers);

			// Create matching pairs
			CardMatchingQuestionDTO pairsDTO = new CardMatchingQuestionDTO();
			pairsDTO.setPrompts(prompts);
			pairsDTO.setAnswers(answers);
			quizQuestionDTO.setPairs(pairsDTO);
		}
		return quizQuestionDTO;
	}

	public static QuizQuestionOptionDTO mapToQuizQuestionOptionDTO(QuizQuestionOption option) {
		QuizQuestionOptionDTO optionDTO = new QuizQuestionOptionDTO();
		optionDTO.setId(option.getId());
		optionDTO.setOptionText1(option.getOptionText1());
		optionDTO.setOptionText2(option.getOptionText2());
		optionDTO.setOptionText3(option.getOptionText3());
		return optionDTO;
	}

	public static CourseDetailDTO mapToCourseDetailDTO(Course course) {
		CourseDetailDTO courseDetailDTO = new CourseDetailDTO();
		courseDetailDTO.setId(course.getId());
		courseDetailDTO.setTitle(course.getTitle());
		courseDetailDTO.setDescription(course.getDescription());
		courseDetailDTO.setObjective(course.getObjective());
		courseDetailDTO.setThumbnailUrl(course.getThumbnail_url());
		courseDetailDTO.setPreviewVideoUrl(course.getPreview_video_url());
		courseDetailDTO.setPrice(course.getPrice().doubleValue());
		courseDetailDTO.setTotalReviews(course.getReviews().size());
		courseDetailDTO.setStudents(course.getEnrollments().size());
		courseDetailDTO.setTotalSections(course.getSections().size());
		courseDetailDTO.setTotalLessons(course.getSections().stream()
				.mapToInt(section -> section.getLessons().size())
				.sum());

		// set sections - order by orderIndex
		Collections.sort(course.getSections(), (a, b) -> Integer.compare(a.getOrderIndex(), b.getOrderIndex()));
		List<CourseSectionDetailDTO> sections = new ArrayList<>();
		for (CourseSection section : course.getSections()) {
			CourseSectionDetailDTO sectionDTO = mapToCourseSectionDetailDTO(section);
			sections.add(sectionDTO);
		}
		courseDetailDTO.setSections(sections);
		return courseDetailDTO;
	}

	public static CourseSectionDetailDTO mapToCourseSectionDetailDTO(CourseSection section) {
		CourseSectionDetailDTO sectionDTO = new CourseSectionDetailDTO();
		sectionDTO.setId(section.getId());
		sectionDTO.setTitle(section.getTitle());
		sectionDTO.setOrderIndex(section.getOrderIndex());
		sectionDTO.setTotalLessons(section.getLessons().size());
		
		// isCompleted will be check in service
		
		// duration calculate from lessons
		int duration = 0;
		for (Lesson lesson : section.getLessons()) {
			duration += lesson.getDuration();
		}
		sectionDTO.setDuration(duration);

		// set lessons - order by orderIndex
		Collections.sort(section.getLessons(), (a, b) -> Integer.compare(a.getOrderIndex(), b.getOrderIndex()));
		List<LessonDetailDTO> lessons = new ArrayList<>();
		for (Lesson lesson : section.getLessons()) {
			LessonDetailDTO lessonDTO = mapToLessonDetailDTO(lesson);
			lessons.add(lessonDTO);
		}
		sectionDTO.setLessons(lessons);
		return sectionDTO;
	}
}