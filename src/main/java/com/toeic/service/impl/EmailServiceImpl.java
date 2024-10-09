package com.toeic.service.impl;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.toeic.service.EmailService;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService{
	
	private final JavaMailSender javaMailSender;

	@Override
	public void sendEmail(String to, String subject, String body) throws MessagingException {
		MimeMessage message = javaMailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
	
		helper.setTo(to);
		helper.setSubject(subject);
		helper.setText(body, true);	// enable HTML content
		
		try {
			javaMailSender.send(message);
		} catch (Exception e) {
			System.err.println("Failed to send email: " + e.getMessage());
			throw e;
		}
	}
	
}
