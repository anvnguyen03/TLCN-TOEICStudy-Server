package com.toeic.service;

import jakarta.servlet.http.HttpServletRequest;

public interface PaymentService {
    String createVnPayPaymentURL(HttpServletRequest request);
}
