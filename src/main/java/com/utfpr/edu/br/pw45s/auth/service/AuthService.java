package com.utfpr.edu.br.pw45s.auth.service;

import com.utfpr.edu.br.pw45s.auth.dto.LoginRequest;
import com.utfpr.edu.br.pw45s.auth.dto.LoginResponse;
import com.utfpr.edu.br.pw45s.shared.security.JwtService;
import com.utfpr.edu.br.pw45s.users.entity.Role;
import com.utfpr.edu.br.pw45s.users.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
	private final AuthenticationManager authenticationManager;
	private final JwtService jwtService;
	private final UserRepository userRepository;

	public AuthService(AuthenticationManager authenticationManager, JwtService jwtService, UserRepository userRepository) {
		this.authenticationManager = authenticationManager;
		this.jwtService = jwtService;
		this.userRepository = userRepository;
	}

	public LoginResponse login(LoginRequest request) {
		var auth = new UsernamePasswordAuthenticationToken(request.email(), request.password());
		authenticationManager.authenticate(auth);
		var user = userRepository.findByEmail(request.email()).orElseThrow();
		var roles = user.getRoles().stream().map(Role::getName).toList();
		String token = jwtService.generateToken(user.getEmail(), roles);
		return new LoginResponse(token, user.getEmail(), roles);
	}
}