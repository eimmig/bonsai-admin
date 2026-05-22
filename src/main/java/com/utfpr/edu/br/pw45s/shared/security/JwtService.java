package com.utfpr.edu.br.pw45s.shared.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

public class JwtService {
	private final JwtProperties properties;

	public JwtService(JwtProperties properties) {
		this.properties = properties;
	}

	public String generateToken(String subject, List<String> roles) {
		Instant now = Instant.now();
		Instant exp = now.plus(properties.expirationMinutes(), ChronoUnit.MINUTES);
		byte[] key = properties.secret().getBytes(StandardCharsets.UTF_8);

		return Jwts.builder()
			.subject(subject)
			.issuer(properties.issuer())
			.issuedAt(Date.from(now))
			.expiration(Date.from(exp))
			.claim("roles", roles)
			.signWith(Keys.hmacShaKeyFor(key))
			.compact();
	}
}