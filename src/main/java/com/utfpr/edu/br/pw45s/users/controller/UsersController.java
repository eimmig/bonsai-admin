package com.utfpr.edu.br.pw45s.users.controller;

import com.utfpr.edu.br.pw45s.shared.base.dto.PageResponse;
import com.utfpr.edu.br.pw45s.users.dto.AssignRolesRequest;
import com.utfpr.edu.br.pw45s.users.dto.RegisterUserRequest;
import com.utfpr.edu.br.pw45s.users.dto.UserMapper;
import com.utfpr.edu.br.pw45s.users.dto.UserResponse;
import com.utfpr.edu.br.pw45s.users.entity.User;
import com.utfpr.edu.br.pw45s.users.service.UsersService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;
@RestController
@RequestMapping("/users")
public class UsersController {
	private final UsersService usersService;
	private final UserMapper mapper = new UserMapper();

	public UsersController(UsersService usersService) {
		this.usersService = usersService;
	}

	@PostMapping("/register")
	public ResponseEntity<UserResponse> register(@RequestBody @Valid RegisterUserRequest request) {
		var user = usersService.register(request.email(), request.password());
		return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponse(user));
	}

	@PreAuthorize("hasRole('ADMIN')")
	@PutMapping("/{id}/activate")
	public ResponseEntity<UserResponse> activate(@PathVariable UUID id) {
		var user = usersService.activate(id);
		return ResponseEntity.ok(mapper.toResponse(user));
	}

	@PreAuthorize("hasRole('ADMIN')")
	@PutMapping("/{id}/roles")
	public ResponseEntity<UserResponse> assignRoles(
		@PathVariable UUID id,
		@RequestBody @Valid AssignRolesRequest request
	) {
		var user = usersService.assignRoles(id, request.roles());
		return ResponseEntity.ok(mapper.toResponse(user));
	}

	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping
	public ResponseEntity<PageResponse<UserResponse>> list(Pageable pageable) {
		Page<User> page = usersService.findAll(pageable);
		var response = new PageResponse<>(
			page.map(mapper::toResponse).toList(),
			page.getNumber(),
			page.getSize(),
			page.getTotalElements(),
			page.getTotalPages()
		);
		return ResponseEntity.ok(response);
	}

	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/{id}")
	public ResponseEntity<UserResponse> getById(@PathVariable UUID id) {
		var user = usersService.findById(id)
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
		return ResponseEntity.ok(mapper.toResponse(user));
	}
}