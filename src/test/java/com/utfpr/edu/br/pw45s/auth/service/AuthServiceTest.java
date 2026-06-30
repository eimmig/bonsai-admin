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
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
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
	void loginFailsWhenAuthenticationThrows() {
		doThrow(new DisabledException("User is disabled"))
			.when(authenticationManager).authenticate(any());

		var request = new LoginRequest("inactive@example.com", "pass");
		ResponseStatusException ex = assertThrows(
			ResponseStatusException.class,
			() -> authService.login(request)
		);

		assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatusCode());
	}

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
