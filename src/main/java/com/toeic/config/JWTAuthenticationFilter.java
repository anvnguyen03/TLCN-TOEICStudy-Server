package com.toeic.config;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.toeic.service.JWTService;
import com.toeic.service.UserService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JWTAuthenticationFilter extends OncePerRequestFilter {
	
	private final JWTService jwtService;
	private final UserService userService;
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
		final String authHeader = request.getHeader("Authorization"); 
		final String jwt;
		final String userEmail;
		
		// check token's existence
		if (authHeader == null || !authHeader.startsWith("Bearer") || authHeader.length() < 8) {
			filterChain.doFilter(request, response);
			return;
		}
		
		jwt = authHeader.substring(7); // token begin with "Bearer "
		userEmail = jwtService.extractUsername(jwt);
		
		if (!userEmail.isEmpty() && SecurityContextHolder.getContext().getAuthentication() == null) {
			UserDetails userDetails = this.userService.userDetailsService().loadUserByUsername(userEmail);
			
			if (jwtService.isTokenValid(jwt)) {
				SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
				
				UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
							userDetails, null, userDetails.getAuthorities());
				
				token.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				
				securityContext.setAuthentication(token);
				SecurityContextHolder.setContext(securityContext);
			}
		}
		filterChain.doFilter(request, response);
	}

}
