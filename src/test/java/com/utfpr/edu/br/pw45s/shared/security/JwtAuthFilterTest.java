package com.utfpr.edu.br.pw45s.shared.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtAuthFilterTest {

	@AfterEach
	void cleanup() {
		SecurityContextHolder.clearContext();
	}

	private static final JwtProperties PROPS = new JwtProperties(
		"01234567890123456789012345678901", "pw45s", 15
	);
	private static final ObjectMapper MAPPER = new ObjectMapper();

	@Test
	void skipsWhenNoAuthorizationHeader() throws Exception {
		JwtAuthFilter filter = new JwtAuthFilter(PROPS, MAPPER);
		MockHttpServletRequest request = new MockHttpServletRequest();
		MockHttpServletResponse response = new MockHttpServletResponse();
		AtomicBoolean chainCalled = new AtomicBoolean(false);
		FilterChain chain = (req, res) -> chainCalled.set(true);

		filter.doFilter(request, response, chain);

		assertTrue(chainCalled.get());
		assertNull(SecurityContextHolder.getContext().getAuthentication());
	}

	@Test
	void returns401WithProblemDetailOnInvalidToken() throws Exception {
		JwtAuthFilter filter = new JwtAuthFilter(PROPS, MAPPER);
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.addHeader("Authorization", "Bearer invalid.token.here");
		MockHttpServletResponse response = new MockHttpServletResponse();
		AtomicBoolean chainCalled = new AtomicBoolean(false);
		FilterChain chain = (req, res) -> chainCalled.set(true);

		filter.doFilter(request, response, chain);

		assertFalse(chainCalled.get());
		assertEquals(401, response.getStatus());
		assertEquals("application/problem+json;charset=UTF-8", response.getContentType());
		assertTrue(response.getContentAsString().contains("\"status\":401"));
	}

	@Test
	void setsAuthenticationFromToken() throws Exception {
		JwtService service = new JwtService(PROPS);
		String token = service.generateToken("user@example.com", List.of("ADMIN"));

		JwtAuthFilter filter = new JwtAuthFilter(PROPS, MAPPER);
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.addHeader("Authorization", "Bearer " + token);
		MockHttpServletResponse response = new MockHttpServletResponse();
		AtomicBoolean chainCalled = new AtomicBoolean(false);
		FilterChain chain = (req, res) -> chainCalled.set(true);

		filter.doFilter(request, response, chain);

		assertTrue(chainCalled.get());
		var auth = SecurityContextHolder.getContext().getAuthentication();
		assertEquals("user@example.com", auth.getName());
		assertEquals(1, auth.getAuthorities().size());
	}
}
