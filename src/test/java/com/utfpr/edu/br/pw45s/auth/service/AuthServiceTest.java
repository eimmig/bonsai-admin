package com.utfpr.edu.br.pw45s.auth.service;

import com.utfpr.edu.br.pw45s.auth.dto.LoginRequest;
import com.utfpr.edu.br.pw45s.shared.security.JwtService;
import com.utfpr.edu.br.pw45s.users.entity.Role;
import com.utfpr.edu.br.pw45s.users.entity.User;
import com.utfpr.edu.br.pw45s.users.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

	@Mock
	private AuthenticationManager authenticationManager;

	@Mock
	private JwtService jwtService;

	@Mock
	private UserRepository userRepository;

	@InjectMocks
	private AuthService authService;

	@Test
	void loginReturnsTokenAndUserInfo() {
		User user = new User("user@example.com", "hash");
		user.getRoles().add(new Role("ADMIN"));

		when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
		when(jwtService.generateToken(any(), any())).thenReturn("token");

		var response = authService.login(new LoginRequest("user@example.com", "pass"));

		verify(authenticationManager).authenticate(any());
		assertEquals("token", response.token());
		assertEquals("user@example.com", response.email());
		assertEquals(1, response.roles().size());
	}
}
