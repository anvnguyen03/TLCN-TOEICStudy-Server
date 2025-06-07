package com.toeic.controller;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.toeic.dto.response.ApiResponse;
import com.toeic.service.CourseEnrollmentService;
import com.toeic.service.PaymentService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/payment")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PaymentController {

    private final PaymentService paymentService;
    private final CourseEnrollmentService courseEnrollmentService;

    @Value("${CLIENT_URL}")
    private String clientUrl;
    
    @GetMapping("/vn-pay")
    public ResponseEntity<ApiResponse<String>> pay(HttpServletRequest request) {
        String paymentUrl = paymentService.createVnPayPaymentURL(request);

        // VNPay sandbox url pay error => enroll course right after create payment url
        // long userId = Long.parseLong(request.getParameter("userId"));
        // long courseId = Long.parseLong(request.getParameter("courseId"));
        // courseEnrollmentService.enrollCourse(userId, courseId);

        ApiResponse<String> response = ApiResponse.success(HttpStatus.OK, "Payment URL created successfully", paymentUrl);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/vn-pay-callback")
    public ResponseEntity<ApiResponse<String>> payCallbackHandler(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String status = request.getParameter("vnp_ResponseCode");
        long userId = Long.parseLong(request.getParameter("userId"));
        long courseId = Long.parseLong(request.getParameter("courseId"));
        // convert courseId from long to string before adding to url
        String courseIdString = String.valueOf(courseId);
        if (status.equals("00")) {
            courseEnrollmentService.enrollCourse(userId, courseId);
            
        	response.sendRedirect(clientUrl + "/payment-success?courseId=" + courseIdString);
            ApiResponse<String> apiResponse = ApiResponse.success(HttpStatus.OK, "Payment successful", "");
            return ResponseEntity.ok(apiResponse);
        } else {
        	response.sendRedirect(clientUrl + "/payment-failed?courseId=" + courseIdString);
            ApiResponse<String> apiResponse = ApiResponse.error(HttpStatus.BAD_REQUEST, "Payment failed", null);
            return ResponseEntity.badRequest().body(apiResponse);
        }
    }
    
}
