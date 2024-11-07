package com.toeic.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.cloudinary.Cloudinary;

@Configuration
public class CloudinaryConfig {

	@Value("${CLOUDINARY_URL}")
	private String cloudinaryURL;
	
	@Bean
	Cloudinary cloudinary() {
		return new Cloudinary(cloudinaryURL);
	}
}
