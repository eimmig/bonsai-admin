package com.utfpr.edu.br.pw45s.shared.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class JwtServiceTest {

	@Test
	void generateTokenIncludesSubjectAndRoles() {
		JwtProperties props = new JwtProperties(
			"01234567890123456789012345678901",
			"pw45s",
			15
		);
		JwtService service = new JwtService(props);

		String token = service.generateToken("user@example.com", List.of("ADMIN", "OPERATOR"));
		assertNotNull(token);

		Claims claims = Jwts.parser()
			.verifyWith(Keys.hmacShaKeyFor(props.secret().getBytes(StandardCharsets.UTF_8)))
			.build()
			.parseSignedClaims(token)
			.getPayload();

		assertEquals("user@example.com", claims.getSubject());
		assertEquals(List.of("ADMIN", "OPERATOR"), claims.get("roles", List.class));
	}
}
