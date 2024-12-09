package com.toeic.service.impl;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.toeic.dto.response.DisplayTestItemDTO;
import com.toeic.dto.response.ETestItemType;
import com.toeic.dto.response.TestInfoDTO;
import com.toeic.dto.response.TestInfoPagingDTO;
import com.toeic.dto.response.UserResultDTO;
import com.toeic.entity.ETestStatus;
import com.toeic.entity.Part;
import com.toeic.entity.Question;
import com.toeic.entity.QuestionGroup;
import com.toeic.entity.QuestionGroupImage;
import com.toeic.entity.Test;
import com.toeic.entity.TestCategory;
import com.toeic.entity.User;
import com.toeic.entity.UserAnswer;
import com.toeic.entity.UserResult;
import com.toeic.exception.ResourceNotFoundException;
import com.toeic.repository.PartRepository;
import com.toeic.repository.QuestionGroupImageRepository;
import com.toeic.repository.QuestionGroupRepository;
import com.toeic.repository.QuestionRepository;
import com.toeic.repository.TestCategoryRepository;
import com.toeic.repository.TestRepository;
import com.toeic.repository.UserAnswerRepository;
import com.toeic.repository.UserResultRepository;
import com.toeic.service.TestService;
import com.toeic.utils.DTOMapperUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TestServiceImpl implements TestService{
	
	private final TestCategoryRepository testCategoryRepository;
	private final TestRepository testRepository;
	private final PartRepository partRepository;
	private final QuestionGroupRepository questionGroupRepository;
	private final QuestionGroupImageRepository questionGroupImageRepository;
	private final QuestionRepository questionRepository;
	private final UserResultRepository userResultRepository;
	private final UserAnswerRepository userAnswerRepository;
	private final Cloudinary cloudinary;
	
	@Override
	@Transactional
	public Test uploadFullTest(MultipartFile file, List<MultipartFile> images, List<MultipartFile> audios) {
		// variable use to delete resource on local and Cloudinary if rollback execute
		String testTitle = null;
		try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
			
			// 1. Handle Test Info
			Sheet testInfoSheet = workbook.getSheet("Test_Info");
			Row testRow = testInfoSheet.getRow(1);	// row(0) contains header
			String title = testRow.getCell(0).getStringCellValue();
			testTitle = title;
			int duration = (int) testRow.getCell(1).getNumericCellValue();
			int totalQuestions = (int) testRow.getCell(2).getNumericCellValue();
			String listeningAudioPath = uploadToLocal(testRow.getCell(3).getStringCellValue(), audios, title);
			
			Test test = new Test();
			test.setTitle(title);
			test.setTotal_questions(totalQuestions);
			test.setDuration(duration);
			test.setListening_audio(listeningAudioPath);
			
			TestCategory testCategory = testCategoryRepository.findByName("Full Test").orElseThrow(() -> new ResourceNotFoundException("Unknown category"));
			test.setTestCategory(testCategory);
			test = testRepository.save(test);
			
			initializeParts(test);	// initialize 7 parts for this test
			
			// 2. Handle Questions
			Sheet questionSheet = workbook.getSheet("Questions");
			List<Question> questions = new ArrayList<>();	// save list questions to link to groups later
			// row(0) contains header
			for (int i = 1; i <= questionSheet.getLastRowNum(); i++) {
				Row row = questionSheet.getRow(i);
				
				int orderNumber = (int) row.getCell(0).getNumericCellValue();
				int partNum = (int) row.getCell(1).getNumericCellValue();
				Cell contentCell = row.getCell(2);
				String content = isBlankCell(contentCell) ? null : contentCell.getStringCellValue();	// part 1-2 do not show question's content
				String answer1 = row.getCell(3).getStringCellValue();
				String answer2 = row.getCell(4).getStringCellValue();
				String answer3 = row.getCell(5).getStringCellValue();
				Cell answer4Cell = row.getCell(6);
				String answer4 = isBlankCell(answer4Cell) ? null : answer4Cell.getStringCellValue();	// part 2's questions only have 3 options
				String correctAnswer = row.getCell(7).getStringCellValue();
				
				Cell transcriptCell = row.getCell(8);
				String transcript = isBlankCell(transcriptCell) ? null : transcriptCell.getStringCellValue();
				
				Cell imagePathCell = row.getCell(9);
				String imagePath = isBlankCell(imagePathCell) ? null : uploadToCloudinary(imagePathCell.getStringCellValue(), images, test.getTitle());
				
				Cell audioPathCell = row.getCell(10);
				String audioPath = isBlankCell(audioPathCell) ? null : uploadToLocal(audioPathCell.getStringCellValue(), audios, test.getTitle());
				
				Question question = new Question();
				question.setOrder_number(orderNumber);
				Part part = partRepository.findByPartNumAndTest(partNum, test).orElseThrow();
				question.setTest(test);
				question.setPart(part);
				question.setContent(content);
				question.setAnswer_1(answer1);
				question.setAnswer_2(answer2);
				question.setAnswer_3(answer3);
				question.setAnswer_4(answer4);
				question.setCorrect_answer(correctAnswer);
				question.setTranscript(transcript);
				question.setImage(imagePath);
				question.setAudio(audioPath);
				
				question = questionRepository.save(question);
				questions.add(question);
			}
			
			// 3. Handle Question Groups
			Sheet questionGroupSheet = workbook.getSheet("Question_Groups");
			// row(0) contains header
			for (int i = 1; i <= questionGroupSheet.getLastRowNum(); i++) {
				Row row = questionGroupSheet.getRow(i);
				int partNum = (int) row.getCell(0).getNumericCellValue();
				String groupName = row.getCell(1).getStringCellValue();
				Cell groupContentCell = row.getCell(2);
				String groupContent = isBlankCell(groupContentCell) ? null : groupContentCell.getStringCellValue();
				Cell imagePathsCell = row.getCell(3);
				String[] imagePaths = isBlankCell(imagePathsCell) ? null : imagePathsCell.getStringCellValue().split(",");
				Cell audioPathCell = row.getCell(4);
				String audioPath = isBlankCell(audioPathCell) ? null : uploadToCloudinary(audioPathCell.getStringCellValue(), audios, test.getTitle());
				String questionIndexes = row.getCell(5).getStringCellValue();
				
				QuestionGroup questionGroup = new QuestionGroup();
				questionGroup.setName(groupName);
				questionGroup.setContent(groupContent);
				questionGroup.setAudio(audioPath);
				Part part = partRepository.findByPartNumAndTest(partNum, test).orElseThrow();
				questionGroup.setPart(part);
				questionGroup.setTest(test);
				questionGroup = questionGroupRepository.save(questionGroup);
				
				// One Question Group may have multiple images or none ( group in listening part )
				if (imagePaths != null) {
					List<QuestionGroupImage> questionGroupImages = new ArrayList<>();
					for (String imagePath : imagePaths) {
						String uploadedImagePath = uploadToCloudinary(imagePath.trim(), images, test.getTitle());
						QuestionGroupImage questionGroupImage = new QuestionGroupImage();
						questionGroupImage.setImage(uploadedImagePath);
						questionGroupImage.setQuestionGroup(questionGroup);
						questionGroupImage = questionGroupImageRepository.save(questionGroupImage);
						questionGroupImages.add(questionGroupImage);
					}
					questionGroup.setQuestionGroupImages(questionGroupImages);
					questionGroup = questionGroupRepository.save(questionGroup);
				}
				
				// Associate Questions to QuestionGroup after Questions have been saved
				String[] indexes = questionIndexes.split(",");
				for (String index : indexes) {
					Question question = questions.stream()
							.filter(q -> q.getOrder_number() == Integer.parseInt(index.trim()))
							.findFirst()
							.orElseThrow();
					question.setQuestionGroup(questionGroup);
					questionRepository.save(question);
				}	
			}
			
			// 4. Handle listening test timestamp
			Sheet timestampSheet = workbook.getSheet("Listening_Timestamp");
			// row(0) contains header
			for (int i = 1; i <= timestampSheet.getLastRowNum(); i++) {
				Row row = timestampSheet.getRow(i);
				String type = row.getCell(0).getStringCellValue().trim();
				float start_timestamp = (float) row.getCell(2).getNumericCellValue();
				
				switch (type) {
					case "Part": {
						int partNum = (int) row.getCell(1).getNumericCellValue();
						
						Part part = partRepository.findByPartNumAndTest(partNum, test).orElseThrow();
						part.setStart_timestamp(start_timestamp);
						partRepository.save(part);
						break;
					}
					case "Question": {
						int orderNumber = (int) row.getCell(1).getNumericCellValue();
						
						Question question = questionRepository.findByOrderNumberAndTest(orderNumber, test).orElseThrow();
						question.setStart_timestamp(start_timestamp);
						questionRepository.save(question);
						break;
					}
					case "Question Group": {
						String groupName = row.getCell(1).getStringCellValue();
						
						QuestionGroup questionGroup = questionGroupRepository.findByNameAndTest(groupName, test).orElseThrow();
						questionGroup.setStart_timestamp(start_timestamp);
						questionGroupRepository.save(questionGroup);
						break;
					}
					default:
						throw new IllegalArgumentException("Unexpected value: " + type);
				}
			}
			
			return test;
			
		} catch (Exception e) {
			System.out.println("Title: " + testTitle);
			e.printStackTrace();
			throw new RuntimeException("Error processing uploaded files", e);
		}
	}
	
	private void initializeParts(Test test) {
		for (int i=1; i<=7; i++) {
			Part newPart = new Part();
			newPart.setPart_num(i);
			newPart.setTest(test);
			partRepository.save(newPart);
		}
	}
	
	private boolean isBlankCell(Cell cell) {
		return cell == null || cell.getCellType() == CellType.BLANK || cell.toString().trim().isEmpty();
	}
	
	private String uploadToCloudinary(String fileName, List<MultipartFile> resources, String testTitle) {
		for (MultipartFile resource : resources) {
			if (resource.getOriginalFilename().equalsIgnoreCase(fileName)) {
				try {
					Map params = ObjectUtils.asMap(
							"folder", "TOEIC-Study/" + testTitle,
						    "public_id", resource.getOriginalFilename()
						);
					
					Map uploadResult = cloudinary.uploader().upload(resource.getBytes(), params);
					return (String) uploadResult.get("secure_url");
				} catch (IOException e) {
					e.printStackTrace();
					throw new RuntimeException("Error uploading to Cloudinary", e);
				}
			}
		}
		String fileNotFound = "File: " + fileName + " not found in resources";
		return fileNotFound;
	}
	
	@Value("${FILE.UPLOAD_DIR}")
    private String uploadDir;

    @Value("${FILE.URL_PREFIX}")
    private String urlPrefix;
    
	private String uploadToLocal(String fileName, List<MultipartFile> resources, String testTitle) {
		for (MultipartFile resource : resources) {
			if (resource.getOriginalFilename().equalsIgnoreCase(fileName)) {
				try {
					// Sử dụng System.getProperty("user.dir") cho môi trường local
					Path uploadAudioPath = Paths.get(System.getProperty("user.dir"), "src", "main", "resources", "static", "audios", testTitle);
					// hoặc uploadDir cho môi trường deploy
					// Path uploadAudioPath = Paths.get(uploadDir, "audios", testTitle);
					if (!Files.exists(uploadAudioPath)) {
						Files.createDirectories(uploadAudioPath);
					}
					
					String audioName = resource.getOriginalFilename();
					Path audioFile = uploadAudioPath.resolve(audioName);
					
					try (OutputStream osAudio = Files.newOutputStream(audioFile)) {
						osAudio.write(resource.getBytes());
					}
					// trả về đường dẫn trực tiếp request đến source trên server cho trường audio
					return urlPrefix + "audios/" + testTitle + "/" + audioName;
				} catch (Exception e) {
					e.printStackTrace();
					throw new RuntimeException("Error uploading to local", e);
				}
			}
		}
		String fileNotFound = "File: " + fileName + " not found in resources";
		return fileNotFound;
	}

	@Override
	public TestInfoDTO getTestInfo(long testId) {
		Test test = testRepository.findById(testId).orElseThrow(() -> new ResourceNotFoundException("Test not found"));
		TestInfoDTO testInfo = DTOMapperUtils.mapToTestInfoDTO(test);
		return testInfo;
	}

	@Override
	public List<TestInfoDTO> getAllTestInfo() {
		List<Test> tests = testRepository.findAll();
		if (tests != null) {
			List<TestInfoDTO> testsInfo = tests.stream().map(DTOMapperUtils::mapToTestInfoDTO)
												.collect(Collectors.toList());
			return testsInfo;
		}
		return null;
	}

	@Override
	public List<DisplayTestItemDTO> getDisplayTestItem(long testId) {
		Test test = testRepository.findById(testId).orElseThrow(() -> new ResourceNotFoundException("Test not found"));
		List<DisplayTestItemDTO> displayItems = new ArrayList<>();
		
		test.getParts().stream()
			.sorted(Comparator.comparingInt(Part::getPart_num))	// Ensure part order from 1->7
			.forEach(part -> {
				// Create displayItem for PART
				DisplayTestItemDTO partItem = new DisplayTestItemDTO();
				partItem.setType(ETestItemType.PART);
				partItem.setStartTimestamp(part.getStart_timestamp());
				partItem.setPart(DTOMapperUtils.mapToPartDTO(part));
				displayItems.add(partItem);
				
				// Check whether Part has Question Groups
				if (!part.getQuestionGroups().isEmpty()) {
					// Traverse through all group and create displayItem for QUESTION_GROUP
					part.getQuestionGroups().forEach(group -> {
						DisplayTestItemDTO groupItem = new DisplayTestItemDTO();
						groupItem.setType(ETestItemType.QUESTION_GROUP);
						groupItem.setStartTimestamp(group.getStart_timestamp());
						groupItem.setQuestionGroup(DTOMapperUtils.mapToQuestionGroupDTO(group));
						displayItems.add(groupItem);
					});
				} else {
					// Part has Question
					part.getQuestions().forEach(question -> {
						// Traverse through all question and create displayItem for QUESTION
						DisplayTestItemDTO questionItem = new DisplayTestItemDTO();
						questionItem.setType(ETestItemType.QUESTION);
						questionItem.setStartTimestamp(question.getStart_timestamp());
						questionItem.setQuestion(DTOMapperUtils.mapToQuestionDTO(question));
						displayItems.add(questionItem);
					});
					
				}
			});
		
		return displayItems;
	}

	@Override
	public UserResultDTO getUserResult(User user, long resultId) {
		UserResult userResult = userResultRepository.findById(resultId)
				.orElseThrow(() -> new ResourceNotFoundException("Unknown user result"));
		if (!user.equals(userResult.getUser())) {
			throw new ResourceNotFoundException("Unknown result for this user");
		}
		
		UserResultDTO userResultDTO = DTOMapperUtils.mapToUserResultDTO(userResult);
		return userResultDTO;
	}

	@Override
	public TestInfoPagingDTO getByPublishedStatusAndKeywordWithPagination(String keyword, Long testCategoryId, int page, int size, User user) {
		// keyword rỗng/null => dùng kí tự đại diện findAll
		String searchKeyword = (keyword == null || keyword.trim().isEmpty()) ? "" : keyword;
		
		Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
		
		Page<Test> testPage = null;
		if (testCategoryId == null) {
			testPage = testRepository
					.findByStatusAndTitleContainingIgnoreCase(ETestStatus.PUBLISHED, searchKeyword, pageable);
		} else {
			testPage = testRepository
					.findByStatusAndTestCategoryIdAndTitleContainingIgnoreCase(ETestStatus.PUBLISHED, testCategoryId, searchKeyword, pageable);
		}
		
		Page<TestInfoDTO> testDTOPage = testPage.map(test -> DTOMapperUtils.mapToTestInfoDTO(test));
		
		if (user == null) {
			return DTOMapperUtils.mapToTestInfoPagingDTO(testDTOPage);
		}
		
		// Nếu có user đăng nhập và fetch test, phải set lại thuộc tính isUserAttemped trong TestInfoDTO ( mặc định = false )
		List<TestInfoDTO> testDTOList = testDTOPage.getContent();
		testDTOList.forEach(testDTO -> {
			// Kiểm tra trong UserResultRepo xem có tồn tại record nào của test và user này không
			boolean isUserAttemped = userResultRepository.existsByTestIdAndUserId(testDTO.getId(), user.getId());
			testDTO.setUserAttemped(isUserAttemped);
		});
		
		 // Tạo lại Page<TestDTO> từ danh sách đã chỉnh sửa
		Page<TestInfoDTO> updatedTestDTOList = new PageImpl<>(testDTOList, pageable, testDTOPage.getTotalElements());
        return DTOMapperUtils.mapToTestInfoPagingDTO(updatedTestDTOList);
	}

	@Override
	public TestInfoDTO getTestInfoForLoggedInUser(User user, long testId) {
		Test test = testRepository.findById(testId).orElseThrow(() -> new ResourceNotFoundException("Test not found"));
		TestInfoDTO testInfo = DTOMapperUtils.mapToTestInfoDTO(test);
		boolean isUserAttemped = userResultRepository.existsByTestIdAndUserId(testId, user.getId());
		testInfo.setUserAttemped(isUserAttemped);
		return testInfo;
	}

	@Override
	public List<UserResultDTO> getUserResultsForUser(User user, long testId) {
		List<UserResultDTO> userResultsDTO = userResultRepository.findByTestIdAndUserId(testId, user.getId())
																.stream()
																.map(DTOMapperUtils::mapToUserResultDTO)
																.collect(Collectors.toList());
		
		return userResultsDTO;
	}

	@Override
	@Transactional
	public void deleteTest(long testId) {
		try {
			Test test = testRepository.findById(testId).orElseThrow(() -> new ResourceNotFoundException("Unknown test"));
			
			List<UserResult> results = userResultRepository.findByTestId(testId);
			for (UserResult result : results) {
				
				List<UserAnswer> answers = userAnswerRepository.findByUserResultId(result.getId());
				for (UserAnswer answer : answers) {
					userAnswerRepository.delete(answer);
				}
				
				userResultRepository.delete(result);
			}
			
			List<Question> questions = questionRepository.findByTestId(testId);
			for (Question question : questions) {
				questionRepository.delete(question);
			}
			
			List<QuestionGroup> groups = questionGroupRepository.findByTestId(testId);
			for (QuestionGroup group : groups) {
				
				List<QuestionGroupImage> groupImages = questionGroupImageRepository.findByQuestionGroupId(group.getId());
				for (QuestionGroupImage groupImage : groupImages) {
					questionGroupImageRepository.delete(groupImage);
				}
				
				questionGroupRepository.delete(group);
			}
			
			List<Part> parts = partRepository.findByTestId(testId);
			for (Part part : parts) {
				partRepository.delete(part);
			}
			
			Map<String, Object> options = ObjectUtils.asMap(
		            "keep_original", false // Nếu xóa bản gốc
		        );
			Map deleteResult = cloudinary.api().deleteResourcesByPrefix("TOEIC-Study/" + test.getTitle(), options);
			System.out.println("Cloudinary delete result: " + deleteResult);
			
			Path audiosPath = Paths.get(System.getProperty("user.dir"), "src", "main", "resources", "static", "audios", test.getTitle());
			deleteDirectoryRecursively(audiosPath);
			
			testRepository.delete(test);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Error while deleting test", e);
		}
	}
	
	private static void deleteDirectoryRecursively(Path path) throws IOException {
	    Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
	        @Override
	        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
	            Files.delete(file);
	            return FileVisitResult.CONTINUE;
	        }

	        @Override
	        public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
	            if (exc == null) {
	                Files.delete(dir);
	                return FileVisitResult.CONTINUE;
	            } else {
	                throw exc;
	            }
	        }
	    });
	}

}
