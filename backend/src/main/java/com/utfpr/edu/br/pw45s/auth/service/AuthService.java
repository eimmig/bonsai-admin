package com.utfpr.edu.br.pw45s.auth.service;

import com.utfpr.edu.br.pw45s.auth.dto.LoginRequest;
import com.utfpr.edu.br.pw45s.auth.dto.LoginResponse;
import com.utfpr.edu.br.pw45s.shared.security.JwtService;
import com.utfpr.edu.br.pw45s.users.entity.Role;
import com.utfpr.edu.br.pw45s.users.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {
	private static final Logger log = LoggerFactory.getLogger(AuthService.class);

	private final AuthenticationManager authenticationManager;
	private final JwtService jwtService;
	private final UserRepository userRepository;

	public AuthService(AuthenticationManager authenticationManager, JwtService jwtService, UserRepository userRepository) {
		this.authenticationManager = authenticationManager;
		this.jwtService = jwtService;
		this.userRepository = userRepository;
	}

	public LoginResponse login(LoginRequest request) {
		try {
			authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.email(), request.password()));
		} catch (AuthenticationException ex) {
			log.warn("Login failed for '{}': {}", request.email(), ex.getMessage());
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
		}
		var user = userRepository.findByEmail(request.email()).orElseThrow();
		var roles = user.getRoles().stream().map(Role::getName).toList();
		String token = jwtService.generateToken(user.getEmail(), roles);
		log.info("Login successful for '{}'", request.email());
		return new LoginResponse(token, user.getEmail(), roles);
	}
}