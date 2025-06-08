package com.toeic.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/api/v1/admin/course-upload")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CourseUploadController {

}
