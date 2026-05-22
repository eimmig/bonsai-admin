package com.utfpr.edu.br.pw45s.auth.controller;

import com.utfpr.edu.br.pw45s.auth.dto.LoginRequest;
import com.utfpr.edu.br.pw45s.auth.dto.LoginResponse;
import com.utfpr.edu.br.pw45s.auth.service.AuthService;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AuthControllerTest {

	@Test
	void loginDelegatesToService() {
		AuthService authService = mock(AuthService.class);
		AuthController controller = new AuthController(authService);
		LoginResponse response = new LoginResponse("token", "user@example.com", List.of("ADMIN"));

		when(authService.login(new LoginRequest("user@example.com", "pass"))).thenReturn(response);

		var result = controller.login(new LoginRequest("user@example.com", "pass"));
		assertEquals("token", result.getBody().token());
	}
}
