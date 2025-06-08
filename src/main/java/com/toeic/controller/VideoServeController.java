package com.toeic.controller;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.toeic.entity.Lesson;
import com.toeic.entity.User;
import com.toeic.exception.ResourceNotFoundException;
import com.toeic.repository.LessonRepository;
import com.toeic.service.AccountService;
import com.toeic.service.CourseEnrollmentService;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/api/v1/video-serve")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class VideoServeController {

    private final AccountService accountService;
    private final CourseEnrollmentService courseEnrollmentService;

    private final LessonRepository lessonRepository;

    @Value("${COURSE.VIDEO.BASE_PATH}")
    private String basePath;

    /**
     * Serve video content to the client.
     * 
     * do not use @RequestHeader("Authorization") to get token 
     * because the client-side get stream video Since <video> tags or ReactPlayer don't send the Authorization header automatically, 
     * the token must be passed via query parameters for the server to authenticate and allow video streaming.
     */
    @GetMapping("/stream/{courseId}/{lessonId}/{filename}")
    public ResponseEntity<Resource> serveVideo(
            @PathVariable Long courseId,
            @PathVariable Long lessonId,
            @PathVariable String filename,
            @RequestHeader(value = "Range", required = false) String rangeHeader,
            HttpServletRequest request) throws IOException {

        // get token from request parameter since the client-side get stream video 
        String token = request.getParameter("token");
        if (token == null) {
            throw new RuntimeException("Token is required");
        }
        User user = accountService.fetchAccount(token);

        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson not found"));

        // check if lesson is free or user is enrolled in course
        if (!lesson.getIsFree()) {
            if (!courseEnrollmentService.isEnrolled(user.getId(), courseId)) {
                throw new RuntimeException("User is not enrolled in course");
            }
        }

        Path videoPath = Paths.get(basePath, courseId.toString(), filename);
        if (!Files.exists(videoPath)) {
            throw new ResourceNotFoundException("Video not found");
        }

        long contentLength = Files.size(videoPath);
        String contentType = MediaTypeFactory
                .getMediaType(videoPath.getFileName().toString())
                .orElse(MediaType.APPLICATION_OCTET_STREAM)
                .toString();

        // no range header → return full content
        if (rangeHeader == null) {
            Resource fullResource = new FileSystemResource(videoPath);
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .contentLength(contentLength)
                    .body(fullResource);
        }

        // has range header → return partial content
        long start = Long.parseLong(rangeHeader.replace("bytes=", "").split("-")[0]);
        long end = contentLength - 1;
        long rangeLength = end - start + 1;

        InputStream inputStream = Files.newInputStream(videoPath);
        inputStream.skip(start);
        InputStreamResource partialResource = new InputStreamResource(inputStream);

        return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                .header("Content-Range", String.format("bytes %d-%d/%d", start, end, contentLength))
                .header("Accept-Ranges", "bytes")
                .contentType(MediaType.parseMediaType(contentType))
                .contentLength(rangeLength)
                .body(partialResource);
    }
}
