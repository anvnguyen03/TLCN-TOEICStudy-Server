package com.toeic.service.impl;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.toeic.config.VnpayConfig;
import com.toeic.service.PaymentService;
import com.toeic.utils.VNPayUtil;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final VnpayConfig vnPayConfig;
    
    @Override
    public String createVnPayPaymentURL(HttpServletRequest request) {
        long price = Integer.parseInt(request.getParameter("price")) * 100L;
        
        long userId = Long.parseLong(request.getParameter("userId"));
        long courseId = Long.parseLong(request.getParameter("courseId"));
        
        Map<String, String> vnpParamsMap = vnPayConfig.getVNPayConfig(String.valueOf(userId), String.valueOf(courseId));
        vnpParamsMap.put("vnp_Amount", String.valueOf(price));
        vnpParamsMap.put("vnp_IpAddr", VNPayUtil.getIpAddress(request));
        //build query url
        String queryUrl = VNPayUtil.getPaymentURL(vnpParamsMap, true);
        String hashData = VNPayUtil.getPaymentURL(vnpParamsMap, false);
        String vnpSecureHash = VNPayUtil.hmacSHA512(vnPayConfig.getSecretKey(), hashData);
        queryUrl += "&vnp_SecureHash=" + vnpSecureHash;
        String paymentUrl = vnPayConfig.getVnp_PayUrl() + "?" + queryUrl;
        
        return paymentUrl;
    }
}
