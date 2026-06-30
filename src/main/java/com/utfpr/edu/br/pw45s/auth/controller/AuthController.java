package com.utfpr.edu.br.pw45s.auth.controller;

import com.utfpr.edu.br.pw45s.auth.dto.LoginRequest;
import com.utfpr.edu.br.pw45s.auth.dto.LoginResponse;
import com.utfpr.edu.br.pw45s.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Autenticação")
@RestController
@RequestMapping("/auth")
public class AuthController {
	private final AuthService authService;

	public AuthController(AuthService authService) {
		this.authService = authService;
	}

	@SecurityRequirements
	@Operation(summary = "Autenticar", description = "Retorna um JWT válido por 60 minutos.")
	@ApiResponse(responseCode = "200", description = "Autenticado com sucesso")
	@ApiResponse(responseCode = "401", description = "Credenciais inválidas ou usuário inativo")
	@PostMapping("/login")
	public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest request) {
		return ResponseEntity.ok(authService.login(request));
	}
}