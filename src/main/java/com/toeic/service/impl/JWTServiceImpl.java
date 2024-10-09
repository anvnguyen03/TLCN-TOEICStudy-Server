package com.toeic.service.impl;

import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.toeic.entity.User;
import com.toeic.repository.UserRepository;
import com.toeic.service.JWTService;
import com.toeic.service.UserService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JWTServiceImpl implements JWTService {
	
	@Value("${JWT_SECRET}")
	private String secretKey;
	private final UserRepository userRepository;
	private final UserService userService;
	
	@Override
	public String extractUsername(String token) {
		return extractClaim(token, Claims::getSubject);
	}

	private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = extractAllClaims(token);
		return claimsResolver.apply(claims);
	}

	private Claims extractAllClaims(String token) {
		return Jwts
				.parserBuilder()
				.setSigningKey(getSigninKey())
				.build()
				.parseClaimsJws(token)
				.getBody();
				
	}
	
	@Override
	public String generateToken(UserDetails userDetails) {
		String role = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
		
		User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow();
		String fullname = user.getFullname();
		String userId = String.valueOf(user.getId());
		return Jwts.builder().setSubject(userDetails.getUsername())
				.claim("role", role)
				.claim("userId", userId)
				.claim("fullname", fullname)
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + 1000*60*60*24))	// 1 day
				.signWith(getSigninKey(), SignatureAlgorithm.HS256)
				.compact();
	}
	
	private Key getSigninKey() {
		byte[] key = Decoders.BASE64.decode(secretKey);
		return Keys.hmacShaKeyFor(key);
	}

	@Override
	public String generateRefreshToken(Map<String, Object> extraClaims, UserDetails userDetails) {
		return Jwts.builder().setClaims(extraClaims).setSubject(userDetails.getUsername())
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + 604800000))	// 7 days
				.signWith(getSigninKey(), SignatureAlgorithm.HS256)
				.compact();
	}

	@Override
	public boolean isTokenValid(String token) {
		try {
			final String username = extractUsername(token);
			
			if (username == null || username.isEmpty()) {
				return false;
			}
			
			UserDetails userDetails = userService.userDetailsService().loadUserByUsername(username);
			return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
		} catch (Exception e) {
			return false;
		}
	}
	
	private boolean isTokenExpired(String token) {
		return extractClaim(token, Claims::getExpiration).before(new Date());
	}

	@Override
	public String generateActiveToken(UserDetails userDetails) {
		
		return Jwts.builder().setSubject(userDetails.getUsername())
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + 1000*60*5))	// 5 mins
				.signWith(getSigninKey(), SignatureAlgorithm.HS256)
				.compact();
	}

}
