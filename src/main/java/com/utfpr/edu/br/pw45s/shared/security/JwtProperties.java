package com.utfpr.edu.br.pw45s.shared.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "security.jwt")
public record JwtProperties(
	String secret,
	String issuer,
	long expirationMinutes
) {
}