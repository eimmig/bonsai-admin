package com.utfpr.edu.br.pw45s.shared.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class JwtAuthFilter extends OncePerRequestFilter {
	private final JwtProperties properties;

	public JwtAuthFilter(JwtProperties properties) {
		this.properties = properties;
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
		byte[] key = properties.secret().getBytes(StandardCharsets.UTF_8);
		Claims claims = Jwts.parser()
			.verifyWith(io.jsonwebtoken.security.Keys.hmacShaKeyFor(key))
			.build()
			.parseSignedClaims(token)
			.getPayload();

		String subject = claims.getSubject();
		List<String> roles = claims.get("roles", List.class);
		var authorities = roles.stream()
			.map(r -> new SimpleGrantedAuthority("ROLE_" + r))
			.toList();

		var auth = new UsernamePasswordAuthenticationToken(subject, null, authorities);
		SecurityContextHolder.getContext().setAuthentication(auth);
		filterChain.doFilter(request, response);
	}
}