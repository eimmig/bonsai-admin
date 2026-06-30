package com.utfpr.edu.br.pw45s.shared.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class JwtAuthFilter extends OncePerRequestFilter {
	private static final Logger log = LoggerFactory.getLogger(JwtAuthFilter.class);

	private final JwtProperties properties;
	private final ObjectMapper objectMapper;

	public JwtAuthFilter(JwtProperties properties, ObjectMapper objectMapper) {
		this.properties = properties;
		this.objectMapper = objectMapper;
	}

	@Override
	protected void doFilterInternal(
		HttpServletRequest request,
		HttpServletResponse response,
		FilterChain filterChain
	) throws ServletException, IOException {
		String header = request.getHeader("Authorization");
		if (header == null || !header.startsWith("Bearer ")) {
			filterChain.doFilter(request, response);
			return;
		}

		String token = header.substring(7);
		Claims claims;
		try {
			byte[] key = properties.secret().getBytes(StandardCharsets.UTF_8);
			claims = Jwts.parser()
				.verifyWith(io.jsonwebtoken.security.Keys.hmacShaKeyFor(key))
				.build()
				.parseSignedClaims(token)
				.getPayload();
		} catch (JwtException _) {
			log.warn("Invalid or expired JWT on {} {}", request.getMethod(), request.getRequestURI());
			ProblemDetail detail = ProblemDetail.forStatus(HttpStatus.UNAUTHORIZED);
			detail.setTitle("Unauthorized");
			detail.setDetail("Token inválido ou expirado");
			response.setStatus(HttpStatus.UNAUTHORIZED.value());
			response.setContentType("application/problem+json;charset=UTF-8");
			response.getWriter().write(objectMapper.writeValueAsString(detail));
			return;
		}

		String subject = claims.getSubject();
		@SuppressWarnings("unchecked")
		List<String> roles = claims.get("roles", List.class);
		var authorities = roles.stream()
			.map(r -> new SimpleGrantedAuthority("ROLE_" + r))
			.toList();

		var auth = new UsernamePasswordAuthenticationToken(subject, null, authorities);
		SecurityContextHolder.getContext().setAuthentication(auth);
		filterChain.doFilter(request, response);
	}
}