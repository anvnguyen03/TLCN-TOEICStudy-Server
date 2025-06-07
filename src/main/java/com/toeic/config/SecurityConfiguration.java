package com.toeic.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.authentication.AuthenticationProvider;

import com.toeic.entity.ERole;
import com.toeic.service.UserService;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

	private final JWTAuthenticationFilter jwtAuthenticationFilter;
	private final UserService userService;
	private String[] publicApiUrls = {
			"/ws/**",
			"/images/**",
			"/audios/**",
			"/api/v1/auth/**",
			"/api/v1/test/**",
			"/api/v1/test-category/**",
			"/api/v1/course/**",
			"/api/v1/payment/**"
	};
	
	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		
		http
			.csrf(csrf -> csrf.disable())
			.authorizeHttpRequests(request -> request
												.requestMatchers(publicApiUrls).permitAll()

												.requestMatchers("/api/v1/user/**", 
																"/api/v1/account/**",
																"/api/v1/do-test/**",
																"/api/v1/comment/**",
																"/api/v1/do-course/**").hasAnyAuthority(ERole.USER.name(), ERole.ADMIN.name())
												.requestMatchers("/api/v1/admin/**").hasAnyAuthority(ERole.ADMIN.name())

												.anyRequest().authenticated())
			.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.authenticationProvider(AuthenticationProvider())
//			.addFilterBefore(customCorsFilter, UsernamePasswordAuthenticationFilter.class)		// same position, added first - execute first
			.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
		
		return http.build();
	}

	@Bean
	AuthenticationProvider AuthenticationProvider() {
		DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
		authenticationProvider.setUserDetailsService(userService.userDetailsService());
		authenticationProvider.setPasswordEncoder(passwordEncoder());
		return authenticationProvider;
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
		return config.getAuthenticationManager();
	}
	
}
