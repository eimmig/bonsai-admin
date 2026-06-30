package com.utfpr.edu.br.pw45s.users.controller;

import com.utfpr.edu.br.pw45s.shared.base.dto.PageResponse;
import com.utfpr.edu.br.pw45s.users.dto.AssignRolesRequest;
import com.utfpr.edu.br.pw45s.users.dto.RegisterUserRequest;
import com.utfpr.edu.br.pw45s.users.dto.UserMapper;
import com.utfpr.edu.br.pw45s.users.dto.UserResponse;
import com.utfpr.edu.br.pw45s.users.service.UsersService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Tag(name = "Usuários")
@RestController
@RequestMapping("/users")
public class UsersController {
	private final UsersService usersService;
	private final UserMapper mapper;

	public UsersController(UsersService usersService, UserMapper mapper) {
		this.usersService = usersService;
		this.mapper = mapper;
	}

	@SecurityRequirements
	@Operation(summary = "Registrar usuário", description = "Cria conta inativa; um admin precisa ativá-la.")
	@ApiResponse(responseCode = "201", description = "Usuário criado")
	@ApiResponse(responseCode = "400", description = "Email já cadastrado")
	@PostMapping("/register")
	public ResponseEntity<UserResponse> register(@RequestBody @Valid RegisterUserRequest request) {
		var user = usersService.register(request.email(), request.password());
		return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponse(user));
	}

	@Operation(summary = "Ativar usuário", description = "Requer ADMIN.")
	@ApiResponse(responseCode = "200", description = "Usuário ativado")
	@ApiResponse(responseCode = "404", description = "Usuário não encontrado")
	@PreAuthorize("hasRole('ADMIN')")
	@PutMapping("/{id}/activate")
	public ResponseEntity<UserResponse> activate(@PathVariable UUID id) {
		var user = usersService.activate(id);
		return ResponseEntity.ok(mapper.toResponse(user));
	}

	@Operation(summary = "Atribuir roles", description = "Substitui todas as roles do usuário. Requer ADMIN.")
	@ApiResponse(responseCode = "200", description = "Roles atualizadas")
	@ApiResponse(responseCode = "400", description = "Role inválida")
	@ApiResponse(responseCode = "404", description = "Usuário não encontrado")
	@PreAuthorize("hasRole('ADMIN')")
	@PutMapping("/{id}/roles")
	public ResponseEntity<UserResponse> assignRoles(
		@PathVariable UUID id,
		@RequestBody @Valid AssignRolesRequest request
	) {
		var user = usersService.assignRoles(id, request.roles());
		return ResponseEntity.ok(mapper.toResponse(user));
	}

	@Operation(summary = "Listar usuários", description = "Paginado. Requer ADMIN.")
	@ApiResponse(responseCode = "200", description = "Página de usuários")
	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping
	public ResponseEntity<PageResponse<UserResponse>> list(Pageable pageable) {
		return ResponseEntity.ok(PageResponse.of(usersService.findAll(pageable).map(mapper::toResponse)));
	}

	@Operation(summary = "Buscar usuário por ID", description = "Requer ADMIN.")
	@ApiResponse(responseCode = "200", description = "Usuário encontrado")
	@ApiResponse(responseCode = "404", description = "Usuário não encontrado")
	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/{id}")
	public ResponseEntity<UserResponse> getById(@PathVariable UUID id) {
		var user = usersService.findById(id)
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
		return ResponseEntity.ok(mapper.toResponse(user));
	}
}
