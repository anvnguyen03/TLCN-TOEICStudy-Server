package com.toeic.service.impl;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.toeic.dto.request.CreateCardMatchingPairRequest;
import com.toeic.dto.request.CreateCourseRequest;
import com.toeic.dto.request.CreateLessonRequest;
import com.toeic.dto.request.CreateQuizQuestionOptionRequest;
import com.toeic.dto.request.CreateQuizQuestionRequest;
import com.toeic.dto.request.CreateSectionRequest;
import com.toeic.dto.response.CourseCardDTO;
import com.toeic.dto.response.CourseDetailDTO;
import com.toeic.dto.response.CourseInfoDTO;
import com.toeic.dto.response.CourseReviewDTO;
import com.toeic.dto.response.FullCourseDTO;
import com.toeic.dto.response.LessonDetailDTO;
import com.toeic.entity.CardMatchingPair;
import com.toeic.entity.Course;
import com.toeic.entity.CourseReview;
import com.toeic.entity.CourseSection;
import com.toeic.entity.ECourseStatus;
import com.toeic.entity.ELessonType;
import com.toeic.entity.EQuizType;
import com.toeic.entity.Lesson;
import com.toeic.entity.QuizQuestion;
import com.toeic.entity.QuizQuestionOption;
import com.toeic.exception.ResourceNotFoundException;
import com.toeic.repository.CourseRepository;
import com.toeic.repository.CourseReviewRepository;
import com.toeic.repository.CourseSectionRepository;
import com.toeic.repository.LessonCompletionRepository;
import com.toeic.repository.LessonRepository;
import com.toeic.service.CourseService;
import com.toeic.utils.DTOMapperUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final CourseReviewRepository courseReviewRepository;
    private final LessonCompletionRepository lessonCompletionRepository;
    private final CourseSectionRepository courseSectionRepository;
    private final LessonRepository lessonRepository;
    private final Cloudinary cloudinary;

    @Value("${COURSE.VIDEO.BASE_PATH}")
    private String videoBasePath;

    @Override
    @Transactional
    public CourseInfoDTO createCourse(CreateCourseRequest request) throws IOException {
        try {
            // 1. Upload thumbnail to Cloudinary
            String thumbnailUrl = uploadThumbnailToCloudinary(request.getThumbnail(), request.getTitle());

            // 2. Create and save course
            Course course = new Course();
            course.setTitle(request.getTitle());
            course.setDescription(request.getDescription());
            course.setObjective(request.getObjective());
            course.setThumbnailUrl(thumbnailUrl);
            course.setPreviewVideoUrl(request.getPreviewVideoUrl());
            course.setPrice(request.getPrice());
            course.setStatus(request.getStatus());
            course.setLevel(request.getLevel());
            course = courseRepository.save(course);

            // 3. Create sections and lessons
            List<CourseSection> sections = new ArrayList<>();
            for (CreateSectionRequest sectionRequest : request.getSections()) {
                CourseSection section = new CourseSection();
                section.setCourse(course);
                section.setTitle(sectionRequest.getTitle());
                section.setOrderIndex(sectionRequest.getOrderIndex());
                section = courseSectionRepository.save(section);

                List<Lesson> lessons = new ArrayList<>();
                for (CreateLessonRequest lessonRequest : sectionRequest.getLessons()) {
                    Lesson lesson = new Lesson();
                    lesson.setSection(section);
                    lesson.setTitle(lessonRequest.getTitle());
                    lesson.setDescription(lessonRequest.getDescription());
                    lesson.setType(ELessonType.valueOf(lessonRequest.getType()));
                    lesson.setDuration(lessonRequest.getDuration());
                    lesson.setOrderIndex(lessonRequest.getOrderIndex());
                    lesson.setIsFree(lessonRequest.getIsFree());

                    // Handle different lesson types
                    switch (lesson.getType()) {
                        case TEXT:
                            lesson.setContent(lessonRequest.getContent());
                            break;
                        case VIDEO:
                            if (lessonRequest.getVideo() != null) {
                                String videoUrl = uploadVideoToLocal(lessonRequest.getVideo(), course.getId());
                                lesson.setVideoUrl(videoUrl);
                            }
                            break;
                        case QUIZ:
                            if (lessonRequest.getQuizQuestions() != null) {
                                List<QuizQuestion> quizQuestions = lessonRequest.getQuizQuestions().stream()
                                    .map(q -> createQuizQuestion(q, lesson))
                                    .collect(Collectors.toList());
                                lesson.setQuizQuestions(quizQuestions);
                            }
                            break;
                    }
                    lessonRepository.save(lesson);
                    lessons.add(lesson);
                }
                section.setLessons(lessons);
                section = courseSectionRepository.save(section);
                sections.add(section);
            }

            course.setSections(sections);
            course = courseRepository.save(course);

            return DTOMapperUtils.mapToCourseInfoDTO(course);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Error creating course: " + e.getMessage(), e);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error creating course: " + e.getMessage(), e);
        }
    }

    private String uploadThumbnailToCloudinary(MultipartFile file, String courseTitle) throws IOException {
        try {
            // Sanitize course title and filename
            String sanitizedTitle = courseTitle.replaceAll("[^a-zA-Z0-9-]", "-").toLowerCase();
            String sanitizedFilename = file.getOriginalFilename().replaceAll("[^a-zA-Z0-9.-]", "-").toLowerCase();
            
            Map params = ObjectUtils.asMap(
                "folder", "TOEIC-Study/" + sanitizedTitle,
                "public_id", sanitizedFilename
            );
            
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), params);
            return (String) uploadResult.get("secure_url");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error uploading course thumbnail to Cloudinary", e);
        }
    }

    private String uploadVideoToLocal(MultipartFile file, Long courseId) throws IOException {
        Path videoPath = Paths.get(System.getProperty("user.dir"), videoBasePath, courseId.toString());
        if (!Files.exists(videoPath)) {
            Files.createDirectories(videoPath);
        }

        String videoName = file.getOriginalFilename();
        Path videoFile = videoPath.resolve(videoName);

        try (OutputStream os = Files.newOutputStream(videoFile)) {
            os.write(file.getBytes());
            return videoName;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error uploading course video: " + videoName + " to local", e);
        }
    }

    private QuizQuestion createQuizQuestion(CreateQuizQuestionRequest request, Lesson lesson) {
        QuizQuestion quizQuestion = new QuizQuestion();
        quizQuestion.setLesson(lesson);
        quizQuestion.setQuestion(request.getQuestion());
        quizQuestion.setType(EQuizType.valueOf(request.getType()));
        quizQuestion.setOrderIndex(request.getOrderIndex());

        // Creation depend on type of question (multiple choice, card matching)
        switch (request.getType()) {
            case "MULTIPLE_CHOICE":
                if (request.getOption() != null) {
                    QuizQuestionOption option = new QuizQuestionOption();
                    option.setQuizQuestion(quizQuestion);
                    option.setOptionText1(request.getOption().getOptionText1());
                    option.setOptionText2(request.getOption().getOptionText2());
                    option.setOptionText3(request.getOption().getOptionText3());
                    option.setCorrectOption(request.getOption().getCorrectOption());
                    quizQuestion.setOption(option);
                }
                break;

            case "CARD_MATCHING":
                if (request.getPairs() != null) {
                    List<CardMatchingPair> pairs = request.getPairs().stream()
                        .map(p -> createCardMatchingPair(p, quizQuestion))
                        .collect(Collectors.toList());
                    quizQuestion.setPairs(pairs);
                }
                break;

            default:
                throw new IllegalArgumentException("Unsupported quiz type: " + request.getType());
        }

        return quizQuestion;
    }

    private CardMatchingPair createCardMatchingPair(CreateCardMatchingPairRequest request, QuizQuestion quizQuestion) {
        CardMatchingPair pair = new CardMatchingPair();
        pair.setQuizQuestion(quizQuestion);
        pair.setPrompt(request.getPrompt());
        pair.setAnswer(request.getAnswer());
        pair.setOrderIndex(request.getOrderIndex());
        return pair;
    }

    @Override
    public List<FullCourseDTO> getAllCoursesDetailForAdmin() {
        List<Course> courses = courseRepository.findAll();
        List<FullCourseDTO> courseDetailDTOs = courses.stream()
                .map(course -> {
                    FullCourseDTO dto = DTOMapperUtils.mapToFullCourseDTO(course);
                    return dto;
                })
                .collect(Collectors.toList());

        return courseDetailDTOs;
    }

    @Override
    public List<CourseCardDTO> getPublishedCourses() {
        List<Course> courses = courseRepository.findByStatus(ECourseStatus.PUBLISHED);
        return courses.stream()
                .map(course -> {
                    CourseCardDTO dto = DTOMapperUtils.mapToCourseCardDTO(course);
                    // Calculate average rating
                    double avgRating = course.getReviews().stream()
                            .mapToDouble(CourseReview::getRating)
                            .average()
                            .orElse(0);
                    dto.setRating(avgRating);
                    
                    // Calculate total lessons
                    int totalLessons = course.getSections().stream()
                            .mapToInt(section -> section.getLessons().size())
                            .sum();
                    dto.setLessons(totalLessons);
                    
                    // Calculate total students
                    int totalStudents = course.getEnrollments().size();
                    dto.setStudents(totalStudents);
    
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public CourseInfoDTO getCourseById(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));
        
        CourseInfoDTO dto = DTOMapperUtils.mapToCourseInfoDTO(course);

        // Calculate rating
        double rating = course.getReviews().stream()
                .mapToDouble(CourseReview::getRating)
                .average()
                .orElse(0);
        dto.setRating(rating);

        // Calculate duration
        int duration = course.getSections().stream()
                .mapToInt(section -> section.getLessons().stream()
                        .mapToInt(Lesson::getDuration)
                        .sum())
                .sum();
        dto.setDuration(duration);

        return dto;
    }

    @Override
    public List<CourseReviewDTO> getCourseRecentReviews(Long courseId) {
        List<CourseReview> reviews = courseReviewRepository.findByCourseIdOrderByCreatedAtDesc(courseId, Pageable.ofSize(3));
        return reviews.stream()
                .map(DTOMapperUtils::mapToCourseReviewDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<LessonDetailDTO> getFreeLessonsOfCourse(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));

        List<Lesson> freeLessons = course.getSections().stream()
                .flatMap(section -> section.getLessons().stream())
                .filter(lesson -> lesson.getIsFree())
                .collect(Collectors.toList());

        // map to DTO
        List<LessonDetailDTO> lessonDetailDTOs = freeLessons.stream()
                .map(DTOMapperUtils::mapToLessonDetailDTO)
                .collect(Collectors.toList());

        return lessonDetailDTOs;

    }

    @Override   
    public CourseDetailDTO getCompleteCourseDetail(Long courseId, Long userId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));

        CourseDetailDTO dto = DTOMapperUtils.mapToCourseDetailDTO(course);

        // calculate duration
        int duration = course.getSections().stream()
                .mapToInt(section -> section.getLessons().stream()
                        .mapToInt(Lesson::getDuration)
                        .sum())
                .sum();
        dto.setDuration(duration);

        // Calculate total completed lessons and set completion status
        int totalCompletedLessons = dto.getSections().stream()
            .flatMap(section -> section.getLessons().stream())
            .mapToInt(lesson -> {
                boolean isCompleted = lessonCompletionRepository.existsByUserIdAndLessonId(userId, lesson.getId());
                lesson.setIsCompleted(isCompleted);
                return isCompleted ? 1 : 0;
            })
            .sum();
        dto.setTotalCompletedLessons(totalCompletedLessons);
        return dto;
    }

}